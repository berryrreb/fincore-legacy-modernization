package com.fincore.risk.dto;

import java.util.Objects;

/**
 * Legacy verbose DTO representing portfolio KPI summary.
 *
 * LEGACY PAIN POINT FOR LAB MIGRATION:
 * Over 120 lines of boilerplate fields, constructor, getters, equals, hashCode, and toString.
 *
 * JAVA 21 TARGET:
 * Replace this entire class with a concise Java 21 'record':
 * public record CreditSummaryDto(
 *     int totalApplications,
 *     int approvedCount,
 *     int conditionalCount,
 *     int rejectedCount,
 *     int pendingBureauCount,
 *     double totalRequestedVolume,
 *     double averageInterestRate,
 *     String oldestReferenceCode,
 *     String newestReferenceCode
 * ) {}
 */
public class CreditSummaryDto {

    private final int totalApplications;
    private final int approvedCount;
    private final int conditionalCount;
    private final int rejectedCount;
    private final int pendingBureauCount;
    private final double totalRequestedVolume;
    private final double averageInterestRate;
    private final String oldestReferenceCode;
    private final String newestReferenceCode;

    public CreditSummaryDto(int totalApplications, int approvedCount, int conditionalCount,
                            int rejectedCount, int pendingBureauCount, double totalRequestedVolume,
                            double averageInterestRate, String oldestReferenceCode, String newestReferenceCode) {
        this.totalApplications = totalApplications;
        this.approvedCount = approvedCount;
        this.conditionalCount = conditionalCount;
        this.rejectedCount = rejectedCount;
        this.pendingBureauCount = pendingBureauCount;
        this.totalRequestedVolume = totalRequestedVolume;
        this.averageInterestRate = averageInterestRate;
        this.oldestReferenceCode = oldestReferenceCode;
        this.newestReferenceCode = newestReferenceCode;
    }

    public int getTotalApplications() {
        return totalApplications;
    }

    public int getApprovedCount() {
        return approvedCount;
    }

    public int getConditionalCount() {
        return conditionalCount;
    }

    public int getRejectedCount() {
        return rejectedCount;
    }

    public int getPendingBureauCount() {
        return pendingBureauCount;
    }

    public double getTotalRequestedVolume() {
        return totalRequestedVolume;
    }

    public double getAverageInterestRate() {
        return averageInterestRate;
    }

    public String getOldestReferenceCode() {
        return oldestReferenceCode;
    }

    public String getNewestReferenceCode() {
        return newestReferenceCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditSummaryDto that = (CreditSummaryDto) o;
        return totalApplications == that.totalApplications &&
                approvedCount == that.approvedCount &&
                conditionalCount == that.conditionalCount &&
                rejectedCount == that.rejectedCount &&
                pendingBureauCount == that.pendingBureauCount &&
                Double.compare(that.totalRequestedVolume, totalRequestedVolume) == 0 &&
                Double.compare(that.averageInterestRate, averageInterestRate) == 0 &&
                Objects.equals(oldestReferenceCode, that.oldestReferenceCode) &&
                Objects.equals(newestReferenceCode, that.newestReferenceCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalApplications, approvedCount, conditionalCount, rejectedCount,
                pendingBureauCount, totalRequestedVolume, averageInterestRate,
                oldestReferenceCode, newestReferenceCode);
    }

    @Override
    public String toString() {
        return "CreditSummaryDto{" +
                "totalApplications=" + totalApplications +
                ", approvedCount=" + approvedCount +
                ", conditionalCount=" + conditionalCount +
                ", rejectedCount=" + rejectedCount +
                ", pendingBureauCount=" + pendingBureauCount +
                ", totalRequestedVolume=" + totalRequestedVolume +
                ", averageInterestRate=" + averageInterestRate +
                ", oldestReferenceCode='" + oldestReferenceCode + '\'' +
                ", newestReferenceCode='" + newestReferenceCode + '\'' +
                '}';
    }
}
