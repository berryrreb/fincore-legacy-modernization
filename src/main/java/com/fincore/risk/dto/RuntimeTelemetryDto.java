package com.fincore.risk.dto;

import java.util.Objects;

/**
 * Legacy verbose DTO exposing live JVM and Spring Boot runtime telemetry to the Web Dashboard.
 *
 * JAVA 21 TARGET: Convert to a concise Java 21 'record'.
 */
public class RuntimeTelemetryDto {

    private final String javaVersion;
    private final String javaVendor;
    private final String jvmName;
    private final String springBootVersion;
    private final boolean modernJava21;
    private final String concurrencyEngine;
    private final boolean virtualThreadsEnabled;
    private final long usedMemoryMb;
    private final long maxMemoryMb;

    public RuntimeTelemetryDto(String javaVersion, String javaVendor, String jvmName,
                               String springBootVersion, boolean modernJava21,
                               String concurrencyEngine, boolean virtualThreadsEnabled,
                               long usedMemoryMb, long maxMemoryMb) {
        this.javaVersion = javaVersion;
        this.javaVendor = javaVendor;
        this.jvmName = jvmName;
        this.springBootVersion = springBootVersion;
        this.modernJava21 = modernJava21;
        this.concurrencyEngine = concurrencyEngine;
        this.virtualThreadsEnabled = virtualThreadsEnabled;
        this.usedMemoryMb = usedMemoryMb;
        this.maxMemoryMb = maxMemoryMb;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public String getJavaVendor() {
        return javaVendor;
    }

    public String getJvmName() {
        return jvmName;
    }

    public String getSpringBootVersion() {
        return springBootVersion;
    }

    public boolean isModernJava21() {
        return modernJava21;
    }

    public String getConcurrencyEngine() {
        return concurrencyEngine;
    }

    public boolean isVirtualThreadsEnabled() {
        return virtualThreadsEnabled;
    }

    public long getUsedMemoryMb() {
        return usedMemoryMb;
    }

    public long getMaxMemoryMb() {
        return maxMemoryMb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuntimeTelemetryDto that = (RuntimeTelemetryDto) o;
        return modernJava21 == that.modernJava21 &&
                virtualThreadsEnabled == that.virtualThreadsEnabled &&
                usedMemoryMb == that.usedMemoryMb &&
                maxMemoryMb == that.maxMemoryMb &&
                Objects.equals(javaVersion, that.javaVersion) &&
                Objects.equals(javaVendor, that.javaVendor) &&
                Objects.equals(jvmName, that.jvmName) &&
                Objects.equals(springBootVersion, that.springBootVersion) &&
                Objects.equals(concurrencyEngine, that.concurrencyEngine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(javaVersion, javaVendor, jvmName, springBootVersion,
                modernJava21, concurrencyEngine, virtualThreadsEnabled, usedMemoryMb, maxMemoryMb);
    }

    @Override
    public String toString() {
        return "RuntimeTelemetryDto{" +
                "javaVersion='" + javaVersion + '\'' +
                ", javaVendor='" + javaVendor + '\'' +
                ", jvmName='" + jvmName + '\'' +
                ", springBootVersion='" + springBootVersion + '\'' +
                ", modernJava21=" + modernJava21 +
                ", concurrencyEngine='" + concurrencyEngine + '\'' +
                ", virtualThreadsEnabled=" + virtualThreadsEnabled +
                ", usedMemoryMb=" + usedMemoryMb +
                ", maxMemoryMb=" + maxMemoryMb +
                '}';
    }
}
