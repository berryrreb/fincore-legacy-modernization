package com.fincore.risk.config;

import com.fincore.risk.entity.CreditApplication;
import com.fincore.risk.model.LoanProduct;
import com.fincore.risk.model.RiskAssessment;
import com.fincore.risk.repository.CreditApplicationRepository;
import com.fincore.risk.service.RiskCalculatorService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * Seeds realistic Banking Credit Origination records into the in-memory H2 database at startup.
 *
 * LEGACY PAIN POINT: Uses javax.annotation.PostConstruct (must migrate to jakarta.annotation.PostConstruct in Java 21 / Spring Boot 3).
 */
@Component
public class DataInitializer {

    private final CreditApplicationRepository repository;
    private final RiskCalculatorService riskCalculatorService;

    public DataInitializer(CreditApplicationRepository repository,
                           RiskCalculatorService riskCalculatorService) {
        this.repository = repository;
        this.riskCalculatorService = riskCalculatorService;
    }

    @PostConstruct
    public void seedInitialPortfolio() {
        if (repository.count() > 0) {
            return;
        }

        List<CreditApplication> seeds = Arrays.asList(
                buildSeed("FIN-2026-001", "Grupo Industrial Monterrey S.A.", "CORPORATE", 1250000.0, 60, 785, 420000.0, 950000.0),
                buildSeed("FIN-2026-002", "María Fernanda Castellanos", "MORTGAGE", 240000.0, 240, 760, 8500.0, 340000.0),
                buildSeed("FIN-2026-003", "Comercializadora Andina PyME", "SME", 85000.0, 36, 715, 22000.0, 95000.0),
                buildSeed("FIN-2026-004", "Carlos Eduardo Mendoza", "CONSUMER", 18500.0, 24, 740, 4200.0, 0.0),
                buildSeed("FIN-2026-005", "Logística del Pacífico S.A. de C.V.", "SME", 160000.0, 48, 655, 28000.0, 80000.0),
                buildSeed("FIN-2026-006", "Roberto Gómez Villaseñor", "MORTGAGE", 310000.0, 180, 645, 7200.0, 330000.0),
                buildSeed("FIN-2026-007", "Ana Sofía Valenzuela", "CONSUMER", 25000.0, 36, 580, 1900.0, 0.0),
                buildSeed("FIN-2026-008", "Agroexportadora Río Grande", "CORPORATE", 890000.0, 48, 710, 210000.0, 320000.0),
                buildSeed("FIN-2026-009", "Tecnologías Cloud Latam S.R.L.", "SME", 110000.0, 36, 730, 35000.0, 130000.0),
                buildSeed("FIN-2026-010", "Jorge Luis Santamaría", "CONSUMER", 12000.0, 18, 665, 2600.0, 0.0),
                buildSeed("FIN-2026-011", "Desarrollos Inmobiliarios Sur", "CORPORATE", 2100000.0, 72, 640, 310000.0, 600000.0),
                buildSeed("FIN-2026-012", "Laura Patricia Orozco", "MORTGAGE", 195000.0, 240, 790, 9100.0, 280000.0)
        );

        repository.saveAll(seeds);
    }

    private CreditApplication buildSeed(String ref, String applicant, String type,
                                        double amount, int term, int score,
                                        double monthlyIncome, double collateral) {
        CreditApplication app = new CreditApplication(
                ref, applicant, type, amount, term, score,
                monthlyIncome, collateral, "MEDIUM", 10.0, "PENDING_BUREAU"
        );
        LoanProduct product = riskCalculatorService.mapToLoanProduct(app);
        RiskAssessment assessment = riskCalculatorService.evaluateProduct(product);
        app.setRiskLevel(assessment.getRiskLevel());
        app.setApprovedRateAnnual(assessment.getAnnualInterestRate());
        app.setStatus(assessment.getRecommendedStatus());
        app.setBureauVerificationCode("PENDING-SYNC");
        app.setProcessedByThread("Initial-Seed (Unverified)");
        return app;
    }
}
