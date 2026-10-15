package com.fincore.risk.model;

import java.util.Objects;

/**
 * Legacy POJO for SME Business Loan (Crédito PyME).
 *
 * JAVA 21 TARGET: Convert to a compact Java 21 record implementing sealed LoanProduct.
 */
public class SmeLoan implements LoanProduct {

    private final double requestedAmount;
    private final int termMonths;
    private final int creditScore;
    private final double annualRevenue;
    private final double collateralValue;

    public SmeLoan(double requestedAmount, int termMonths, int creditScore, double annualRevenue, double collateralValue) {
        this.requestedAmount = requestedAmount;
        this.termMonths = termMonths;
        this.creditScore = creditScore;
        this.annualRevenue = annualRevenue;
        this.collateralValue = collateralValue;
    }

    @Override
    public String getProductCode() {
        return "SME";
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

    public double getAnnualRevenue() {
        return annualRevenue;
    }

    public double getCollateralValue() {
        return collateralValue;
    }

    public double getCollateralCoverageRatio() {
        if (requestedAmount <= 0) {
            return 0.0;
        }
        return collateralValue / requestedAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmeLoan smeLoan = (SmeLoan) o;
        return Double.compare(smeLoan.requestedAmount, requestedAmount) == 0 &&
                termMonths == smeLoan.termMonths &&
                creditScore == smeLoan.creditScore &&
                Double.compare(smeLoan.annualRevenue, annualRevenue) == 0 &&
                Double.compare(smeLoan.collateralValue, collateralValue) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestedAmount, termMonths, creditScore, annualRevenue, collateralValue);
    }

    @Override
    public String toString() {
        return "SmeLoan{" +
                "requestedAmount=" + requestedAmount +
                ", termMonths=" + termMonths +
                ", creditScore=" + creditScore +
                ", annualRevenue=" + annualRevenue +
                ", collateralValue=" + collateralValue +
                '}';
    }
}
