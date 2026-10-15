package com.fincore.risk.service;

import com.fincore.risk.dto.CreditSummaryDto;
import com.fincore.risk.entity.CreditApplication;
import com.fincore.risk.model.ConsumerLoan;
import com.fincore.risk.model.CorporateLineLoan;
import com.fincore.risk.model.LoanProduct;
import com.fincore.risk.model.MortgageLoan;
import com.fincore.risk.model.RiskAssessment;
import com.fincore.risk.model.SmeLoan;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core Banking Credit Risk & Interest Rate Calculator Service.
 *
 * LEGACY PAIN POINTS FOR LAB MIGRATION (JAVA 8 -> JAVA 21):
 * 1. Uses non-thread-safe SimpleDateFormat and mutable java.util.Date instead of java.time.DateTimeFormatter & LocalDateTime.
 * 2. Uses verbose 'if / else if (product instanceof ...)' chains with explicit downcasting instead of
 *    Java 21 Pattern Matching for switch with Guarded Patterns ('case MortgageLoan m when ... ->').
 * 3. Uses Collectors.toList() and manual index math (list.get(0), list.get(list.size() - 1))
 *    instead of Java 21 Stream.toList() and SequencedCollection (list.getFirst(), list.getLast()).
 */
@Service
public class RiskCalculatorService {

    // LEGACY ANTI-PATTERN: SimpleDateFormat is not thread-safe!
    private static final SimpleDateFormat LEGACY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LoanProduct mapToLoanProduct(CreditApplication app) {
        String type = app.getProductType() != null ? app.getProductType().toUpperCase() : "CONSUMER";
        if ("MORTGAGE".equals(type)) {
            return new MortgageLoan(
                    app.getRequestedAmount(),
                    app.getTermMonths(),
                    app.getCreditScore(),
                    app.getCollateralOrPropertyValue()
            );
        } else if ("SME".equals(type)) {
            return new SmeLoan(
                    app.getRequestedAmount(),
                    app.getTermMonths(),
                    app.getCreditScore(),
                    app.getMonthlyIncomeOrRevenue() * 12.0,
                    app.getCollateralOrPropertyValue()
            );
        } else if ("CORPORATE".equals(type)) {
            return new CorporateLineLoan(
                    app.getRequestedAmount(),
                    app.getTermMonths(),
                    app.getCreditScore(),
                    app.getMonthlyIncomeOrRevenue() * 12.0,
                    app.getCollateralOrPropertyValue()
            );
        } else {
            return new ConsumerLoan(
                    app.getRequestedAmount(),
                    app.getTermMonths(),
                    app.getCreditScore(),
                    app.getMonthlyIncomeOrRevenue()
            );
        }
    }

    /**
     * Evaluates risk tier, annual interest rate, and approval recommendation.
     *
     * JAVA 21 REFACTORING TARGET:
     * Replace the 'if / else if (product instanceof ...)' chain with an exhaustive
     * 'switch (product)' using Pattern Matching and 'when' guards over the sealed LoanProduct hierarchy.
     */
    public RiskAssessment evaluateProduct(LoanProduct product) {
        String timestamp = LEGACY_DATE_FORMAT.format(new Date());

        if (product instanceof MortgageLoan) {
            MortgageLoan mortgage = (MortgageLoan) product;
            double ltv = mortgage.getLoanToValueRatio();

            if (mortgage.getCreditScore() >= 740 && ltv <= 0.80) {
                return new RiskAssessment(
                        "LOW",
                        7.25,
                        "APPROVED",
                        "Hipotecario Preferencial: Score excelente y LTV <= 80% (" + Math.round(ltv * 100) + "%)",
                        timestamp
                );
            } else if (mortgage.getCreditScore() >= 660 && ltv <= 0.90) {
                return new RiskAssessment(
                        "MEDIUM",
                        9.10,
                        "APPROVED",
                        "Hipotecario Estándar: Garantía inmobiliaria aceptable (" + Math.round(ltv * 100) + "% LTV)",
                        timestamp
                );
            } else {
                return new RiskAssessment(
                        "HIGH",
                        11.85,
                        "CONDITIONAL",
                        "Hipotecario de Alto Riesgo: Requiere aval adicional o mayor enganche",
                        timestamp
                );
            }

        } else if (product instanceof ConsumerLoan) {
            ConsumerLoan consumer = (ConsumerLoan) product;
            double dti = consumer.getDebtToIncomeRatio();

            if (consumer.getCreditScore() >= 720 && dti <= 0.35) {
                return new RiskAssessment(
                        "LOW",
                        14.50,
                        "APPROVED",
                        "Consumo Prime: Capacidad de pago saludable (DTI " + Math.round(dti * 100) + "%)",
                        timestamp
                );
            } else if (consumer.getCreditScore() >= 620 && dti <= 0.50) {
                return new RiskAssessment(
                        "MEDIUM",
                        19.90,
                        "CONDITIONAL",
                        "Consumo Sub-Prime: Endeudamiento moderado, sujeto a validación de buró",
                        timestamp
                );
            } else {
                return new RiskAssessment(
                        "CRITICAL",
                        28.50,
                        "REJECTED",
                        "Consumo Rechazado: Score insuficiente (" + consumer.getCreditScore() + ") o sobreendeudamiento",
                        timestamp
                );
            }

        } else if (product instanceof SmeLoan) {
            SmeLoan sme = (SmeLoan) product;
            double coverage = sme.getCollateralCoverageRatio();

            if (sme.getCreditScore() >= 700 && coverage >= 1.0) {
                return new RiskAssessment(
                        "LOW",
                        10.75,
                        "APPROVED",
                        "PyME Crecimiento: Garantía líquida/hipotecaria cubre >= 100% del crédito",
                        timestamp
                );
            } else if (sme.getCreditScore() >= 640 && sme.getAnnualRevenue() >= sme.getRequestedAmount() * 1.5) {
                return new RiskAssessment(
                        "MEDIUM",
                        13.40,
                        "APPROVED",
                        "PyME Capital de Trabajo: Flujo anual respalda la operación comercial",
                        timestamp
                );
            } else {
                return new RiskAssessment(
                        "HIGH",
                        17.20,
                        "CONDITIONAL",
                        "PyME Observada: Requiere garantía solidaria de socios principales",
                        timestamp
                );
            }

        } else if (product instanceof CorporateLineLoan) {
            CorporateLineLoan corporate = (CorporateLineLoan) product;

            if (corporate.getCreditScore() >= 750 && corporate.hasStrongCollateralBacking()) {
                return new RiskAssessment(
                        "LOW",
                        6.80,
                        "APPROVED",
                        "Línea Corporativa AAA: Colateral institucional fuerte y EBITDA sólido",
                        timestamp
                );
            } else if (corporate.getCreditScore() >= 680) {
                return new RiskAssessment(
                        "MEDIUM",
                        8.95,
                        "CONDITIONAL",
                        "Línea Corporativa Sindicada: Requiere aprobación de Comité de Riesgo Mayor",
                        timestamp
                );
            } else {
                return new RiskAssessment(
                        "CRITICAL",
                        14.00,
                        "REJECTED",
                        "Línea Corporativa Rechazada: Rating crediticio corporativo bajo el umbral mínimo",
                        timestamp
                );
            }

        } else {
            throw new IllegalArgumentException("Tipo de producto bancario no soportado: " + product);
        }
    }

    /**
     * Computes portfolio summary KPIs using legacy Java 8 stream collectors and index access.
     *
     * JAVA 21 REFACTORING TARGET:
     * Use Stream.toList() and SequencedCollection methods getFirst() / getLast().
     */
    public CreditSummaryDto buildPortfolioSummary(List<CreditApplication> applications) {
        List<CreditApplication> sortedApps = applications.stream()
                .collect(Collectors.toList());

        if (sortedApps.isEmpty()) {
            return new CreditSummaryDto(0, 0, 0, 0, 0, 0.0, 0.0, "N/A", "N/A");
        }

        int approved = (int) sortedApps.stream().filter(a -> "APPROVED".equals(a.getStatus())).count();
        int conditional = (int) sortedApps.stream().filter(a -> "CONDITIONAL".equals(a.getStatus())).count();
        int rejected = (int) sortedApps.stream().filter(a -> "REJECTED".equals(a.getStatus())).count();
        int pending = (int) sortedApps.stream().filter(a -> "PENDING_BUREAU".equals(a.getStatus())).count();

        double totalVolume = sortedApps.stream()
                .mapToDouble(CreditApplication::getRequestedAmount)
                .sum();

        double avgRate = sortedApps.stream()
                .mapToDouble(CreditApplication::getApprovedRateAnnual)
                .average()
                .orElse(0.0);

        // LEGACY INDEX ACCESS: In Java 21, replace with sortedApps.getFirst() and sortedApps.getLast()
        String oldestRef = sortedApps.get(0).getReferenceCode();
        String newestRef = sortedApps.get(sortedApps.size() - 1).getReferenceCode();

        return new CreditSummaryDto(
                sortedApps.size(),
                approved,
                conditional,
                rejected,
                pending,
                Math.round(totalVolume * 100.0) / 100.0,
                Math.round(avgRate * 100.0) / 100.0,
                oldestRef,
                newestRef
        );
    }
}
