package com.fincore.risk.model;

import java.util.Objects;

/**
 * Legacy POJO for Mortgage Loan (Crédito Hipotecario).
 *
 * JAVA 21 TARGET: Convert to a 1-line record implementing sealed LoanProduct.
 */
public class MortgageLoan implements LoanProduct {

    private final double requestedAmount;
    private final int termMonths;
    private final int creditScore;
    private final double propertyValue;

    public MortgageLoan(double requestedAmount, int termMonths, int creditScore, double propertyValue) {
        this.requestedAmount = requestedAmount;
        this.termMonths = termMonths;
        this.creditScore = creditScore;
        this.propertyValue = propertyValue;
    }

    @Override
    public String getProductCode() {
        return "MORTGAGE";
    }

    @Override
    public double getRequestedAmount() {
        return requestedAmount;
    }

    @Override
    public int getTermMonths() {
        return termMonths;
    }

    @Override
    public int getCreditScore() {
        return creditScore;
    }

    public double getPropertyValue() {
        return propertyValue;
    }

    public double getLoanToValueRatio() {
        if (propertyValue <= 0) {
            return 1.0;
        }
        return requestedAmount / propertyValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MortgageLoan that = (MortgageLoan) o;
        return Double.compare(that.requestedAmount, requestedAmount) == 0 &&
                termMonths == that.termMonths &&
                creditScore == that.creditScore &&
                Double.compare(that.propertyValue, propertyValue) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestedAmount, termMonths, creditScore, propertyValue);
    }

    @Override
    public String toString() {
        return "MortgageLoan{" +
                "requestedAmount=" + requestedAmount +
                ", termMonths=" + termMonths +
                ", creditScore=" + creditScore +
                ", propertyValue=" + propertyValue +
                '}';
    }
}
