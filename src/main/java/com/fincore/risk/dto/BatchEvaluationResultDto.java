package com.fincore.risk.dto;

import java.util.List;
import java.util.Objects;

/**
 * Legacy verbose DTO representing the result of a batch Credit Bureau evaluation.
 *
 * JAVA 21 TARGET: Convert to a concise Java 21 'record'.
 */
public class BatchEvaluationResultDto {

    private final int processedCount;
    private final long elapsedTimeMs;
    private final String concurrencyModel;
    private final boolean virtualThreadsActive;
    private final List<String> workerThreadsUsed;
    private final String evaluatedAt;

    public BatchEvaluationResultDto(int processedCount, long elapsedTimeMs, String concurrencyModel,
                                    boolean virtualThreadsActive, List<String> workerThreadsUsed,
                                    String evaluatedAt) {
        this.processedCount = processedCount;
        this.elapsedTimeMs = elapsedTimeMs;
        this.concurrencyModel = concurrencyModel;
        this.virtualThreadsActive = virtualThreadsActive;
        this.workerThreadsUsed = workerThreadsUsed;
        this.evaluatedAt = evaluatedAt;
    }

    public int getProcessedCount() {
        return processedCount;
    }

    public long getElapsedTimeMs() {
        return elapsedTimeMs;
    }

    public String getConcurrencyModel() {
        return concurrencyModel;
    }

    public boolean isVirtualThreadsActive() {
        return virtualThreadsActive;
    }

    public List<String> getWorkerThreadsUsed() {
        return workerThreadsUsed;
    }

    public String getEvaluatedAt() {
        return evaluatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BatchEvaluationResultDto that = (BatchEvaluationResultDto) o;
        return processedCount == that.processedCount &&
                elapsedTimeMs == that.elapsedTimeMs &&
                virtualThreadsActive == that.virtualThreadsActive &&
                Objects.equals(concurrencyModel, that.concurrencyModel) &&
                Objects.equals(workerThreadsUsed, that.workerThreadsUsed) &&
                Objects.equals(evaluatedAt, that.evaluatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processedCount, elapsedTimeMs, concurrencyModel, virtualThreadsActive, workerThreadsUsed, evaluatedAt);
    }

    @Override
    public String toString() {
        return "BatchEvaluationResultDto{" +
                "processedCount=" + processedCount +
                ", elapsedTimeMs=" + elapsedTimeMs +
                ", concurrencyModel='" + concurrencyModel + '\'' +
                ", virtualThreadsActive=" + virtualThreadsActive +
                ", workerThreadsUsed=" + workerThreadsUsed +
                ", evaluatedAt='" + evaluatedAt + '\'' +
                '}';
    }
}
