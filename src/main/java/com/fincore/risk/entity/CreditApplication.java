package com.fincore.risk.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Legacy JPA Entity representing a Banking Credit Application.
 *
 * LEGACY PAIN POINTS FOR LAB MIGRATION:
 * 1. Uses legacy javax.persistence.* and javax.validation.* imports (must migrate to jakarta.* in Spring Boot 3 / Java 21).
 * 2. Uses mutable java.util.Date with @Temporal instead of modern java.time.LocalDateTime.
 */
@Entity
@Table(name = "credit_applications")
public class CreditApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código de referencia es obligatorio")
    @Column(nullable = false, unique = true, length = 32)
    private String referenceCode;

    @NotBlank(message = "El nombre del solicitante o empresa es obligatorio")
    @Column(nullable = false, length = 120)
    private String applicantName;

    @NotBlank(message = "El segmento / producto bancario es obligatorio")
    @Column(nullable = false, length = 32)
    private String productType; // MORTGAGE, CONSUMER, SME, CORPORATE

    @NotNull(message = "El monto solicitado es obligatorio")
    @Min(value = 1000, message = "El monto mínimo es $1,000 USD")
    @Column(nullable = false)
    private Double requestedAmount;

    @NotNull(message = "El plazo en meses es obligatorio")
    @Min(value = 6, message = "El plazo mínimo es 6 meses")
    @Max(value = 360, message = "El plazo máximo es 360 meses")
    @Column(nullable = false)
    private Integer termMonths;

    @NotNull
    @Min(value = 300)
    @Max(value = 850)
    @Column(nullable = false)
    private Integer creditScore;

    @NotNull
    @Column(nullable = false)
    private Double monthlyIncomeOrRevenue;

    @NotNull
    @Column(nullable = false)
    private Double collateralOrPropertyValue;

    @Column(nullable = false, length = 24)
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(nullable = false)
    private Double approvedRateAnnual;

    @Column(nullable = false, length = 24)
    private String status; // PENDING_BUREAU, APPROVED, CONDITIONAL, REJECTED

    @Column(length = 64)
    private String bureauVerificationCode;

    @Column(length = 80)
    private String processedByThread;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastEvaluatedAt;

    public CreditApplication() {
    }

    public CreditApplication(String referenceCode, String applicantName, String productType,
                             Double requestedAmount, Integer termMonths, Integer creditScore,
                             Double monthlyIncomeOrRevenue, Double collateralOrPropertyValue,
                             String riskLevel, Double approvedRateAnnual, String status) {
        this.referenceCode = referenceCode;
        this.applicantName = applicantName;
        this.productType = productType;
        this.requestedAmount = requestedAmount;
        this.termMonths = termMonths;
        this.creditScore = creditScore;
        this.monthlyIncomeOrRevenue = monthlyIncomeOrRevenue;
        this.collateralOrPropertyValue = collateralOrPropertyValue;
        this.riskLevel = riskLevel;
        this.approvedRateAnnual = approvedRateAnnual;
        this.status = status;
        this.createdAt = new Date();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = new Date();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Double getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(Double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }

    public Double getMonthlyIncomeOrRevenue() {
        return monthlyIncomeOrRevenue;
    }

    public void setMonthlyIncomeOrRevenue(Double monthlyIncomeOrRevenue) {
        this.monthlyIncomeOrRevenue = monthlyIncomeOrRevenue;
    }

    public Double getCollateralOrPropertyValue() {
        return collateralOrPropertyValue;
    }

    public void setCollateralOrPropertyValue(Double collateralOrPropertyValue) {
        this.collateralOrPropertyValue = collateralOrPropertyValue;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Double getApprovedRateAnnual() {
        return approvedRateAnnual;
    }

    public void setApprovedRateAnnual(Double approvedRateAnnual) {
        this.approvedRateAnnual = approvedRateAnnual;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBureauVerificationCode() {
        return bureauVerificationCode;
    }

    public void setBureauVerificationCode(String bureauVerificationCode) {
        this.bureauVerificationCode = bureauVerificationCode;
    }

    public String getProcessedByThread() {
        return processedByThread;
    }

    public void setProcessedByThread(String processedByThread) {
        this.processedByThread = processedByThread;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastEvaluatedAt() {
        return lastEvaluatedAt;
    }

    public void setLastEvaluatedAt(Date lastEvaluatedAt) {
        this.lastEvaluatedAt = lastEvaluatedAt;
    }
}
