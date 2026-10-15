package com.fincore.risk.service;

import com.fincore.risk.dto.BatchEvaluationResultDto;
import com.fincore.risk.entity.CreditApplication;
import com.fincore.risk.model.LoanProduct;
import com.fincore.risk.model.RiskAssessment;
import com.fincore.risk.repository.CreditApplicationRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Simulates blocking I/O calls to an External National Credit Bureau & Fraud Prevention API.
 *
 * LEGACY PAIN POINT FOR LAB MIGRATION (JAVA 8 -> JAVA 21):
 * Uses a constrained OS Platform Thread Pool (Executors.newFixedThreadPool(2)).
 * Because each external bureau check blocks for ~220ms waiting on network I/O, evaluating
 * 12 credit applications takes ~1,350ms (6 sequential waves of 2 threads).
 *
 * JAVA 21 REFACTORING TARGET (PROJECT LOOM - VIRTUAL THREADS):
 * Replace Executors.newFixedThreadPool(2) with Executors.newVirtualThreadPerTaskExecutor().
 * All 12 blocking I/O requests will execute concurrently on lightweight Virtual Threads,
 * dropping total batch execution time from ~1,350ms down to ~230ms (6x faster)!
 */
@Service
public class CreditBureauService {

    private static final int SIMULATED_NETWORK_LATENCY_MS = 220;

    // =========================================================================
    // TODO [LAB MISSION 4]: Replace this legacy fixed platform thread pool with
    // Java 21 Virtual Threads: Executors.newVirtualThreadPerTaskExecutor()
    // =========================================================================
    private final ExecutorService bureauExecutor = Executors.newFixedThreadPool(2);

    private final CreditApplicationRepository repository;
    private final RiskCalculatorService riskCalculatorService;

    public CreditBureauService(CreditApplicationRepository repository,
                               RiskCalculatorService riskCalculatorService) {
        this.repository = repository;
        this.riskCalculatorService = riskCalculatorService;
    }

    /**
     * Evaluates all credit applications in the portfolio concurrently against the simulated Credit Bureau.
     */
    public BatchEvaluationResultDto evaluateAllApplicationsInBureau() {
        List<CreditApplication> applications = repository.findAllByOrderByIdAsc();
        long startTime = System.currentTimeMillis();

        List<Callable<CreditApplication>> tasks = new ArrayList<Callable<CreditApplication>>();
        for (final CreditApplication app : applications) {
            tasks.add(new Callable<CreditApplication>() {
                @Override
                public CreditApplication call() throws Exception {
                    return verifySingleApplicationWithBlockingIo(app);
                }
            });
        }

        List<String> threadsObserved = new ArrayList<String>();
        boolean anyVirtual = false;

        try {
            List<Future<CreditApplication>> futures = bureauExecutor.invokeAll(tasks);
            List<CreditApplication> updatedApps = new ArrayList<CreditApplication>();

            for (Future<CreditApplication> future : futures) {
                CreditApplication evaluated = future.get();
                updatedApps.add(evaluated);
                if (evaluated.getProcessedByThread() != null) {
                    threadsObserved.add(evaluated.getProcessedByThread());
                    if (evaluated.getProcessedByThread().contains("VirtualThread")) {
                        anyVirtual = true;
                    }
                }
            }
            repository.saveAll(updatedApps);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error ejecutando consulta masiva al Buró de Crédito", e);
        }

        long elapsedMs = System.currentTimeMillis() - startTime;
        List<String> distinctThreads = threadsObserved.stream()
                .distinct()
                .limit(6)
                .collect(Collectors.toList());

        boolean virtualActive = anyVirtual || isExecutorUsingVirtualThreads();
        String modelLabel = virtualActive
                ? "Java 21 Virtual Threads (Project Loom)"
                : "Legacy Fixed Platform Thread Pool (2 OS Threads)";

        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());

        return new BatchEvaluationResultDto(
                applications.size(),
                elapsedMs,
                modelLabel,
                virtualActive,
                distinctThreads,
                timestamp
        );
    }

    private CreditApplication verifySingleApplicationWithBlockingIo(CreditApplication app) {
        try {
            // Simulates blocking HTTPS call to external Credit Bureau & AML Blacklist service
            Thread.sleep(SIMULATED_NETWORK_LATENCY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Thread current = Thread.currentThread();
        boolean isVirtual = checkIfThreadIsVirtual(current);
        String threadDescriptor = isVirtual
                ? "VirtualThread[" + current.getName() + "#" + current.getId() + "]"
                : current.getName() + " (OS Platform Thread)";

        LoanProduct product = riskCalculatorService.mapToLoanProduct(app);
        RiskAssessment assessment = riskCalculatorService.evaluateProduct(product);

        app.setRiskLevel(assessment.getRiskLevel());
        app.setApprovedRateAnnual(assessment.getAnnualInterestRate());
        app.setStatus(assessment.getRecommendedStatus());
        app.setBureauVerificationCode("BUR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        app.setProcessedByThread(threadDescriptor);
        app.setLastEvaluatedAt(new Date());

        return app;
    }

    /**
     * Dynamically inspects whether the configured bureauExecutor creates Virtual Threads.
     */
    public boolean isExecutorUsingVirtualThreads() {
        try {
            Future<Boolean> probe = bureauExecutor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    return checkIfThreadIsVirtual(Thread.currentThread());
                }
            });
            return Boolean.TRUE.equals(probe.get());
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkIfThreadIsVirtual(Thread thread) {
        try {
            Method isVirtualMethod = Thread.class.getMethod("isVirtual");
            Object result = isVirtualMethod.invoke(thread);
            return Boolean.TRUE.equals(result);
        } catch (Exception ignored) {
            // Running on Java 8 where Thread.isVirtual() does not exist
            return false;
        }
    }
}
