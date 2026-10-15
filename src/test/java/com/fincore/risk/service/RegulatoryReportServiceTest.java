package com.fincore.risk.service;

import com.fincore.risk.entity.CreditApplication;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Legacy Unit Test for RegulatoryReportService using JUnit 4.
 */
public class RegulatoryReportServiceTest {

    private RegulatoryReportService regulatoryReportService;

    @Before
    public void setUp() {
        RiskCalculatorService riskCalculatorService = new RiskCalculatorService();
        regulatoryReportService = new RegulatoryReportService(riskCalculatorService);
    }

    @Test
    public void shouldGenerateValidBaselRegulatoryReportPayload() {
        CreditApplication app = new CreditApplication(
                "FIN-2026-999",
                "Banco Corporativo Prueba S.A.",
                "CORPORATE",
                500000.0,
                48,
                775,
                250000.0,
                300000.0,
                "LOW",
                6.80,
                "APPROVED"
        );

        String payload = regulatoryReportService.generateRegulatoryPayload(app);

        assertNotNull(payload);
        assertTrue(payload.contains("\"regulatoryStandard\": \"BASEL-III-CNBV-2026\""));
        assertTrue(payload.contains("\"referenceCode\": \"FIN-2026-999\""));
        assertTrue(payload.contains("\"committeeDecision\": \"APPROVED\""));
    }
}
