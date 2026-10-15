---
name: java8-legacy-assessment
description: >-
  Audits the FinCore legacy Java 8, Spring Boot 2.7, and Google App Engine codebase to generate a structured technical debt diagnosis and 5-step modernization plan toward Java 21 LTS, Spring Boot 3.3, Virtual Threads, and Google Cloud Run. Use this skill when the user asks to analyze, audit, diagnose, or plan the migration of the repository (Mission 1).
---

# Skill: Diagnóstico de Deuda Técnica y Plan de Modernización (Misión 1)

Usa esta skill cuando el usuario solicite analizar o auditar el repositorio **FinCore** antes de iniciar la migración.

> **IMPORTANTE:** En esta fase **NO debes modificar ningún archivo del repositorio**. Tu objetivo es inspeccionar el código fuente y entregar un diagnóstico ejecutivo claro y accionable.

## Pasos de Ejecución

1. Inspecciona los archivos clave del proyecto:
   - `pom.xml` y `app.yaml`
   - `src/main/java/com/fincore/risk/entity/CreditApplication.java`
   - `src/main/java/com/fincore/risk/controller/CreditDashboardController.java`
   - `src/main/java/com/fincore/risk/config/DataInitializer.java`
   - `src/main/java/com/fincore/risk/model/LoanProduct.java` y sus implementaciones (`MortgageLoan`, `ConsumerLoan`, `SmeLoan`, `CorporateLineLoan`, `RiskAssessment`)
   - `src/main/java/com/fincore/risk/service/RiskCalculatorService.java`, `RegulatoryReportService.java` y `CreditBureauService.java`
   - `src/test/java/com/fincore/risk/service/RiskCalculatorServiceTest.java` y `RegulatoryReportServiceTest.java`

2. Presenta al usuario un **Reporte de Diagnóstico y Plan de Modernización en 5 Fases** que incluya:
   - **Fase 1 (Plataforma y Build - `pom.xml` & Tests):** Salto de Java `1.8` a Java `21`, Spring Boot `2.7.18` a `3.3.5`, y eliminación de `junit-vintage-engine` / JUnit 4 (`@Before`, `org.junit.Test`, `org.junit.Assert`) hacia JUnit 5 Jupiter (`@BeforeEach`, `org.junit.jupiter.api.Test`, `Assertions`).
   - **Fase 2 (Transición a Jakarta EE 10):** Listado exacto de archivos que usan `javax.persistence.*`, `javax.validation.*`, `javax.servlet.*` y `javax.annotation.*` que deben migrar a `jakarta.*`.
   - **Fase 3 (Refactorización Idiomática a Java 21):**
     - `LoanProduct` como `sealed interface` + `MortgageLoan`, `ConsumerLoan`, `SmeLoan`, `CorporateLineLoan` y `RiskAssessment` como `record`.
     - DTOs (`CreditSummaryDto`, `BatchEvaluationResultDto`, `RuntimeTelemetryDto`) como `record`.
     - `RiskCalculatorService`: Reemplazo de `if / else if (product instanceof ...)` por `switch (product)` exhaustivo con Pattern Matching y guardas `when`; reemplazo de `SimpleDateFormat`/`Date` por `DateTimeFormatter`/`LocalDateTime`; uso de `Stream.toList()` y `SequencedCollection` (`getFirst()`, `getLast()`).
     - `RegulatoryReportService`: Reemplazo de concatenación manual de JSON por **Text Blocks (`"""`)** con `.formatted(...)`.
   - **Fase 4 (Concurrencia Moderna con Project Loom):** En `CreditBureauService`, reemplazo del cuello de botella `Executors.newFixedThreadPool(2)` (~1,350 ms para 12 créditos) por `Executors.newVirtualThreadPerTaskExecutor()` (~230 ms, ~6x más rápido) y uso directo de `Thread.isVirtual()`.
   - **Fase 5 (De Google App Engine a Google Cloud Run):** Análisis del `app.yaml` legado (`runtime: java8`, `instance_class: F4`, `max_concurrent_requests: 10`) y estrategia para migrarlo a un contenedor OCI multi-stage con **Eclipse Temurin 21 JRE** en **Google Cloud Run** con alta concurrencia (`--concurrency 80`) impulsada por Virtual Threads.
