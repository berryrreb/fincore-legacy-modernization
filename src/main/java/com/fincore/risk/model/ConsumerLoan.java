package com.fincore.risk.model;

import java.util.Objects;

/**
 * Legacy POJO for Consumer Loan (Préstamo Personal / Consumo).
 *
 * JAVA 21 TARGET: Convert to a compact Java 21 record implementing sealed LoanProduct.
 */
public class ConsumerLoan implements LoanProduct {

    private final double requestedAmount;
    private final int termMonths;
    private final int creditScore;
    private final double monthlyIncome;

    public ConsumerLoan(double requestedAmount, int termMonths, int creditScore, double monthlyIncome) {
        this.requestedAmount = requestedAmount;
        this.termMonths = termMonths;
        this.creditScore = creditScore;
        this.monthlyIncome = monthlyIncome;
    }

    @Override
    public String getProductCode() {
        return "CONSUMER";
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

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public double getDebtToIncomeRatio() {
        if (monthlyIncome <= 0 || termMonths <= 0) {
            return 1.0;
        }
        double estimatedMonthlyInstallment = (requestedAmount / termMonths) * 1.15;
        return estimatedMonthlyInstallment / monthlyIncome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsumerLoan that = (ConsumerLoan) o;
        return Double.compare(that.requestedAmount, requestedAmount) == 0 &&
                termMonths == that.termMonths &&
                creditScore == that.creditScore &&
                Double.compare(that.monthlyIncome, monthlyIncome) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestedAmount, termMonths, creditScore, monthlyIncome);
    }

    @Override
    public String toString() {
        return "ConsumerLoan{" +
                "requestedAmount=" + requestedAmount +
                ", termMonths=" + termMonths +
                ", creditScore=" + creditScore +
                ", monthlyIncome=" + monthlyIncome +
                '}';
    }
}
