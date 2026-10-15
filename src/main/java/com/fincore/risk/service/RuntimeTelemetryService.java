package com.fincore.risk.service;

import com.fincore.risk.dto.RuntimeTelemetryDto;
import org.springframework.boot.SpringBootVersion;
import org.springframework.stereotype.Service;

/**
 * Provides real-time JVM, Spring Boot, and Concurrency Model telemetry to the Web UI Banner.
 */
@Service
public class RuntimeTelemetryService {

    private final CreditBureauService creditBureauService;

    public RuntimeTelemetryService(CreditBureauService creditBureauService) {
        this.creditBureauService = creditBureauService;
    }

    public RuntimeTelemetryDto captureTelemetry() {
        String javaVersion = System.getProperty("java.version", "Unknown");
        String javaVendor = System.getProperty("java.vendor", "Unknown");
        String jvmName = System.getProperty("java.vm.name", "Standard JVM");
        String springVersion = SpringBootVersion.getVersion();

        boolean modernJava21 = isJava21OrHigher(javaVersion) && isSpringBoot3OrHigher(springVersion);
        boolean virtualThreads = creditBureauService.isExecutorUsingVirtualThreads();

        String concurrencyEngine = virtualThreads
                ? "Virtual Threads (Loom Unbounded)"
                : "FixedThreadPool (2 OS Platform Threads)";

        Runtime rt = Runtime.getRuntime();
        long usedMb = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        long maxMb = rt.maxMemory() / (1024 * 1024);

        return new RuntimeTelemetryDto(
                javaVersion,
                javaVendor,
                jvmName,
                springVersion != null ? springVersion : "Unknown",
                modernJava21,
                concurrencyEngine,
                virtualThreads,
                usedMb,
                maxMb
        );
    }

    private boolean isJava21OrHigher(String version) {
        if (version == null || version.startsWith("1.8")) {
            return false;
        }
        try {
            String majorPart = version.split("[._-]")[0];
            int major = Integer.parseInt(majorPart);
            return major >= 21;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isSpringBoot3OrHigher(String springVersion) {
        if (springVersion == null || springVersion.startsWith("2.")) {
            return false;
        }
        try {
            String majorPart = springVersion.split("[._-]")[0];
            int major = Integer.parseInt(majorPart);
            return major >= 3;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
