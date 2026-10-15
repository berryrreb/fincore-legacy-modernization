package com.fincore.risk.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Form payload DTO for creating a new Credit Application from the Web UI.
 *
 * LEGACY PAIN POINT: Uses javax.validation.constraints.* (must migrate to jakarta.validation.constraints.*).
 */
public class NewCreditRequestDto {

    @NotBlank(message = "El nombre del cliente o razón social es obligatorio")
    private String applicantName;

    @NotBlank(message = "Seleccione un producto bancario")
    private String productType; // MORTGAGE, CONSUMER, SME, CORPORATE

    @NotNull(message = "Ingrese el monto solicitado")
    @Min(value = 1000, message = "Monto mínimo $1,000 USD")
    private Double requestedAmount;

    @NotNull(message = "Ingrese el plazo en meses")
    @Min(value = 6, message = "Plazo mínimo 6 meses")
    @Max(value = 360, message = "Plazo máximo 360 meses")
    private Integer termMonths;

    @NotNull(message = "Ingrese el score crediticio preliminar")
    @Min(value = 300, message = "Score mínimo 300")
    @Max(value = 850, message = "Score máximo 850")
    private Integer creditScore;

    @NotNull(message = "Ingrese los ingresos mensuales o facturación")
    @Min(value = 500, message = "El ingreso debe ser mayor a $500")
    private Double monthlyIncomeOrRevenue;

    @NotNull(message = "Ingrese el valor de garantía o propiedad")
    @Min(value = 0)
    private Double collateralOrPropertyValue;

    public NewCreditRequestDto() {
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
}
