package com.fincore.risk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fincore.risk.config.DataInitializer;
import com.fincore.risk.dto.BatchEvaluationResultDto;
import com.fincore.risk.dto.CreditSummaryDto;
import com.fincore.risk.dto.NewCreditRequestDto;
import com.fincore.risk.dto.RuntimeTelemetryDto;
import com.fincore.risk.entity.CreditApplication;
import com.fincore.risk.model.LoanProduct;
import com.fincore.risk.model.RiskAssessment;
import com.fincore.risk.repository.CreditApplicationRepository;
import com.fincore.risk.service.CreditBureauService;
import com.fincore.risk.service.RegulatoryReportService;
import com.fincore.risk.service.RiskCalculatorService;
import com.fincore.risk.service.RuntimeTelemetryService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Web MVC & REST Controller for the FinCore Banking Credit Risk Dashboard.
 *
 * LEGACY PAIN POINT FOR LAB MIGRATION:
 * Uses javax.servlet.http.HttpServletRequest and javax.validation.Valid
 * (must migrate to jakarta.servlet.http.HttpServletRequest and jakarta.validation.Valid in Spring Boot 3).
 */
@Controller
public class CreditDashboardController {

    private final CreditApplicationRepository repository;
    private final RiskCalculatorService riskCalculatorService;
    private final CreditBureauService creditBureauService;
    private final RegulatoryReportService regulatoryReportService;
    private final RuntimeTelemetryService telemetryService;
    private final DataInitializer dataInitializer;
    private final ObjectMapper objectMapper;

    public CreditDashboardController(CreditApplicationRepository repository,
                                     RiskCalculatorService riskCalculatorService,
                                     CreditBureauService creditBureauService,
                                     RegulatoryReportService regulatoryReportService,
                                     RuntimeTelemetryService telemetryService,
                                     DataInitializer dataInitializer,
                                     ObjectMapper objectMapper) {
        this.repository = repository;
        this.riskCalculatorService = riskCalculatorService;
        this.creditBureauService = creditBureauService;
        this.regulatoryReportService = regulatoryReportService;
        this.telemetryService = telemetryService;
        this.dataInitializer = dataInitializer;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public String showDashboard(Model model, HttpServletRequest request) {
        List<CreditApplication> applications = repository.findAllByOrderByIdAsc();
        CreditSummaryDto summaryDto = riskCalculatorService.buildPortfolioSummary(applications);
        RuntimeTelemetryDto telemetryDto = telemetryService.captureTelemetry();

        // Convert DTOs via Jackson Map so Thymeleaf templates work seamlessly whether DTOs are Java 8 POJOs or Java 21 Records
        model.addAttribute("applications", applications);
        model.addAttribute("summary", objectMapper.convertValue(summaryDto, Map.class));
        model.addAttribute("telemetry", objectMapper.convertValue(telemetryDto, Map.class));
        model.addAttribute("requestUri", request.getRequestURI());

        // Detect Google Cloud Run runtime environment (K_SERVICE & K_REVISION are automatically injected by Cloud Run)
        String kService = System.getenv("K_SERVICE");
        String kRevision = System.getenv("K_REVISION");
        boolean cloudRunActive = kService != null && !kService.trim().isEmpty();
        model.addAttribute("cloudRunActive", cloudRunActive);
        model.addAttribute("cloudRunService", cloudRunActive ? kService : "Cloud Shell / Local Preview");
        model.addAttribute("cloudRunRevision", kRevision != null ? kRevision : "legacy-appengine-target");

        if (!model.containsAttribute("newCredit")) {
            NewCreditRequestDto defaultForm = new NewCreditRequestDto();
            defaultForm.setProductType("MORTGAGE");
            defaultForm.setRequestedAmount(180000.0);
            defaultForm.setTermMonths(180);
            defaultForm.setCreditScore(745);
            defaultForm.setMonthlyIncomeOrRevenue(6500.0);
            defaultForm.setCollateralOrPropertyValue(250000.0);
            model.addAttribute("newCredit", defaultForm);
        }

        return "index";
    }

    @PostMapping("/credits")
    public String createCreditApplication(@Valid @ModelAttribute("newCredit") NewCreditRequestDto form,
                                          BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Verifique los datos ingresados en la solicitud de crédito.");
            return "redirect:/";
        }

        long nextNumber = repository.count() + 1;
        String referenceCode = String.format("FIN-2026-%03d", nextNumber);

        CreditApplication app = new CreditApplication(
                referenceCode,
                form.getApplicantName(),
                form.getProductType(),
                form.getRequestedAmount(),
                form.getTermMonths(),
                form.getCreditScore(),
                form.getMonthlyIncomeOrRevenue(),
                form.getCollateralOrPropertyValue(),
                "MEDIUM",
                10.0,
                "PENDING_BUREAU"
        );

        LoanProduct product = riskCalculatorService.mapToLoanProduct(app);
        RiskAssessment assessment = riskCalculatorService.evaluateProduct(product);

        app.setRiskLevel(assessment.getRiskLevel());
        app.setApprovedRateAnnual(assessment.getAnnualInterestRate());
        app.setStatus(assessment.getRecommendedStatus());
        app.setBureauVerificationCode("NEW-ORIGINATION");
        app.setProcessedByThread(Thread.currentThread().getName());

        repository.save(app);

        redirectAttributes.addFlashAttribute("successMessage",
                "Solicitud " + referenceCode + " originada exitosamente (" + assessment.getRecommendedStatus() +
                        " @ " + assessment.getAnnualInterestRate() + "% T.E.A.)");
        return "redirect:/";
    }

    @PostMapping(value = "/api/bureau/evaluate-batch", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<BatchEvaluationResultDto> evaluatePortfolioBatch() {
        BatchEvaluationResultDto result = creditBureauService.evaluateAllApplicationsInBureau();
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/api/credits/{id}/report", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, String>> getRegulatoryReport(@PathVariable("id") Long id) {
        CreditApplication app = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada: " + id));
        String reportPayload = regulatoryReportService.generateRegulatoryPayload(app);
        return ResponseEntity.ok(Collections.singletonMap("payload", reportPayload));
    }

    @GetMapping(value = "/api/telemetry", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RuntimeTelemetryDto> getLiveTelemetry() {
        return ResponseEntity.ok(telemetryService.captureTelemetry());
    }

    @PostMapping("/credits/reset")
    public String resetDemoData(RedirectAttributes redirectAttributes) {
        repository.deleteAll();
        dataInitializer.seedInitialPortfolio();
        redirectAttributes.addFlashAttribute("successMessage",
                "Portafolio de prueba reiniciado con las 12 operaciones bancarias originales.");
        return "redirect:/";
    }
}
