package com.fincore.risk.service;

import com.fincore.risk.dto.CreditSummaryDto;
import com.fincore.risk.entity.CreditApplication;
import com.fincore.risk.model.ConsumerLoan;
import com.fincore.risk.model.CorporateLineLoan;
import com.fincore.risk.model.MortgageLoan;
import com.fincore.risk.model.RiskAssessment;
import com.fincore.risk.model.SmeLoan;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Legacy Unit Test Suite using JUnit 4 (org.junit.Test, org.junit.Before, org.junit.Assert).
 *
 * JAVA 21 / SPRING BOOT 3 MIGRATION TARGET:
 * Migrate to JUnit 5 Jupiter (@BeforeEach, @org.junit.jupiter.api.Test, org.junit.jupiter.api.Assertions)
 * and update any DTO/Model calls if converted to Java 21 records.
 */
public class RiskCalculatorServiceTest {

    private RiskCalculatorService riskCalculatorService;

    @Before
    public void setUp() {
        riskCalculatorService = new RiskCalculatorService();
    }

    @Test
    public void shouldApprovePrimeMortgageWithLowLtv() {
        MortgageLoan primeMortgage = new MortgageLoan(200000.0, 240, 780, 300000.0);
        RiskAssessment result = riskCalculatorService.evaluateProduct(primeMortgage);

        assertNotNull(result);
        assertEquals("LOW", result.getRiskLevel());
        assertEquals("APPROVED", result.getRecommendedStatus());
        assertEquals(7.25, result.getAnnualInterestRate(), 0.01);
    }

    @Test
    public void shouldRejectHighDebtConsumerLoanWithLowScore() {
        ConsumerLoan riskyConsumer = new ConsumerLoan(30000.0, 24, 550, 1500.0);
        RiskAssessment result = riskCalculatorService.evaluateProduct(riskyConsumer);

        assertEquals("CRITICAL", result.getRiskLevel());
        assertEquals("REJECTED", result.getRecommendedStatus());
        assertTrue(result.getAnnualInterestRate() >= 25.0);
    }

    @Test
    public void shouldApproveWellCollateralizedSmeLoan() {
        SmeLoan smeLoan = new SmeLoan(100000.0, 36, 725, 300000.0, 120000.0);
        RiskAssessment result = riskCalculatorService.evaluateProduct(smeLoan);

        assertEquals("LOW", result.getRiskLevel());
        assertEquals("APPROVED", result.getRecommendedStatus());
    }

    @Test
    public void shouldEvaluateCorporateCreditLine() {
        CorporateLineLoan corpLoan = new CorporateLineLoan(1000000.0, 60, 770, 500000.0, 600000.0);
        RiskAssessment result = riskCalculatorService.evaluateProduct(corpLoan);

        assertEquals("LOW", result.getRiskLevel());
        assertEquals("APPROVED", result.getRecommendedStatus());
        assertEquals(6.80, result.getAnnualInterestRate(), 0.01);
    }

    @Test
    public void shouldBuildPortfolioSummaryWithOldestAndNewestCodes() {
        List<CreditApplication> sampleApps = Arrays.asList(
                new CreditApplication("FIN-2026-001", "Cliente A", "MORTGAGE", 100000.0, 120, 760, 5000.0, 150000.0, "LOW", 7.25, "APPROVED"),
                new CreditApplication("FIN-2026-002", "Cliente B", "CONSUMER", 20000.0, 24, 590, 1500.0, 0.0, "CRITICAL", 28.50, "REJECTED")
        );

        CreditSummaryDto summary = riskCalculatorService.buildPortfolioSummary(sampleApps);

        assertEquals(2, summary.getTotalApplications());
        assertEquals(1, summary.getApprovedCount());
        assertEquals(1, summary.getRejectedCount());
        assertEquals("FIN-2026-001", summary.getOldestReferenceCode());
        assertEquals("FIN-2026-002", summary.getNewestReferenceCode());
    }
}
