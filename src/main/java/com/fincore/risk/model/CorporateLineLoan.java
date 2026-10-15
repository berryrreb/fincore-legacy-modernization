package com.fincore.risk.model;

import java.util.Objects;

/**
 * Legacy POJO for Corporate Credit Line (Línea de Crédito Corporativa / Sindicada).
 *
 * JAVA 21 TARGET: Convert to a compact Java 21 record implementing sealed LoanProduct.
 */
public class CorporateLineLoan implements LoanProduct {

    private final double requestedAmount;
    private final int termMonths;
    private final int creditScore;
    private final double ebitda;
    private final double liquidCollateral;

    public CorporateLineLoan(double requestedAmount, int termMonths, int creditScore, double ebitda, double liquidCollateral) {
        this.requestedAmount = requestedAmount;
        this.termMonths = termMonths;
        this.creditScore = creditScore;
        this.ebitda = ebitda;
        this.liquidCollateral = liquidCollateral;
    }

    @Override
    public String getProductCode() {
        return "CORPORATE";
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

    public double getEbitda() {
        return ebitda;
    }

    public double getLiquidCollateral() {
        return liquidCollateral;
    }

    public boolean hasStrongCollateralBacking() {
        return liquidCollateral >= (requestedAmount * 0.50);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CorporateLineLoan that = (CorporateLineLoan) o;
        return Double.compare(that.requestedAmount, requestedAmount) == 0 &&
                termMonths == that.termMonths &&
                creditScore == that.creditScore &&
                Double.compare(that.ebitda, ebitda) == 0 &&
                Double.compare(that.liquidCollateral, liquidCollateral) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestedAmount, termMonths, creditScore, ebitda, liquidCollateral);
    }

    @Override
    public String toString() {
        return "CorporateLineLoan{" +
                "requestedAmount=" + requestedAmount +
                ", termMonths=" + termMonths +
                ", creditScore=" + creditScore +
                ", ebitda=" + ebitda +
                ", liquidCollateral=" + liquidCollateral +
                '}';
    }
}
