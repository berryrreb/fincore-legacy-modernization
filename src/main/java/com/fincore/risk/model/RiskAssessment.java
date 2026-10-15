package com.fincore.risk.model;

import java.util.Objects;

/**
 * Legacy POJO holding the output of a credit risk evaluation.
 *
 * JAVA 21 TARGET: Convert to a Java 21 record (or keep accessors compatible).
 */
public class RiskAssessment {

    private final String riskLevel;         // LOW, MEDIUM, HIGH, CRITICAL
    private final double annualInterestRate; // e.g., 8.50 (%)
    private final String recommendedStatus; // APPROVED, CONDITIONAL, REJECTED
    private final String rationale;
    private final String evaluatedTimestamp;

    public RiskAssessment(String riskLevel, double annualInterestRate, String recommendedStatus,
                          String rationale, String evaluatedTimestamp) {
        this.riskLevel = riskLevel;
        this.annualInterestRate = annualInterestRate;
        this.recommendedStatus = recommendedStatus;
        this.rationale = rationale;
        this.evaluatedTimestamp = evaluatedTimestamp;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }

    public String getRecommendedStatus() {
        return recommendedStatus;
    }

    public String getRationale() {
        return rationale;
    }

    public String getEvaluatedTimestamp() {
        return evaluatedTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RiskAssessment that = (RiskAssessment) o;
        return Double.compare(that.annualInterestRate, annualInterestRate) == 0 &&
                Objects.equals(riskLevel, that.riskLevel) &&
                Objects.equals(recommendedStatus, that.recommendedStatus) &&
                Objects.equals(rationale, that.rationale) &&
                Objects.equals(evaluatedTimestamp, that.evaluatedTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(riskLevel, annualInterestRate, recommendedStatus, rationale, evaluatedTimestamp);
    }

    @Override
    public String toString() {
        return "RiskAssessment{" +
                "riskLevel='" + riskLevel + '\'' +
                ", annualInterestRate=" + annualInterestRate +
                ", recommendedStatus='" + recommendedStatus + '\'' +
                ", rationale='" + rationale + '\'' +
                ", evaluatedTimestamp='" + evaluatedTimestamp + '\'' +
                '}';
    }
}
