package com.fincore.risk.service;

import com.fincore.risk.entity.CreditApplication;
import com.fincore.risk.model.LoanProduct;
import com.fincore.risk.model.RiskAssessment;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Generates Basel III / Banking Regulatory Compliance Reports for Credit Applications.
 *
 * LEGACY PAIN POINTS FOR LAB MIGRATION (JAVA 8 -> JAVA 21):
 * 1. Uses hard-to-read multi-line String concatenation with escaped quotes (\" and \n).
 * 2. Uses java.util.Date and SimpleDateFormat.
 *
 * JAVA 21 REFACTORING TARGET:
 * Replace the concatenated JSON/Report template with a clean Java 21 Text Block (\"\"\" ... \"\"\")
 * using .formatted(...) and java.time.LocalDateTime.
 */
@Service
public class RegulatoryReportService {

    private final RiskCalculatorService riskCalculatorService;

    public RegulatoryReportService(RiskCalculatorService riskCalculatorService) {
        this.riskCalculatorService = riskCalculatorService;
    }

    public String generateRegulatoryPayload(CreditApplication app) {
        LoanProduct product = riskCalculatorService.mapToLoanProduct(app);
        RiskAssessment assessment = riskCalculatorService.evaluateProduct(product);
        String emittedAt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());

        double monthlyPaymentEstimate = (app.getRequestedAmount() * (1 + (app.getApprovedRateAnnual() / 100.0)))
                / app.getTermMonths();

        // LEGACY STRING CONCATENATION HELL: Prime candidate for Java 21 Text Blocks (""")
        String report = "{\n" +
                "  \"regulatoryStandard\": \"BASEL-III-CNBV-2026\",\n" +
                "  \"emittedAt\": \"" + emittedAt + "\",\n" +
                "  \"runtimeJvm\": \"" + System.getProperty("java.version") + "\",\n" +
                "  \"dossier\": {\n" +
                "    \"referenceCode\": \"" + app.getReferenceCode() + "\",\n" +
                "    \"applicantName\": \"" + app.getApplicantName() + "\",\n" +
                "    \"productSegment\": \"" + app.getProductType() + "\",\n" +
                "    \"requestedAmountUsd\": " + String.format("%.2f", app.getRequestedAmount()) + ",\n" +
                "    \"termMonths\": " + app.getTermMonths() + ",\n" +
                "    \"estimatedMonthlyInstallmentUsd\": " + String.format("%.2f", monthlyPaymentEstimate) + "\n" +
                "  },\n" +
                "  \"riskEvaluation\": {\n" +
                "    \"creditBureauScore\": " + app.getCreditScore() + ",\n" +
                "    \"riskCategory\": \"" + app.getRiskLevel() + "\",\n" +
                "    \"approvedAnnualRatePct\": " + String.format("%.2f", app.getApprovedRateAnnual()) + ",\n" +
                "    \"committeeDecision\": \"" + app.getStatus() + "\",\n" +
                "    \"bureauFolio\": \"" + (app.getBureauVerificationCode() != null ? app.getBureauVerificationCode() : "PENDING-SYNC") + "\",\n" +
                "    \"processedByThread\": \"" + (app.getProcessedByThread() != null ? app.getProcessedByThread() : "N/A") + "\",\n" +
                "    \"technicalRationale\": \"" + assessment.getRationale() + "\"\n" +
                "  }\n" +
                "}";

        return report;
    }
}
