---
description: Estándares de arquitectura y modernización a Java 21 LTS para aplicaciones bancarias FinCore
---

# Reglas de Modernización Bancaria FinCore (Java 21 LTS & Spring Boot 3)

Cuando se realicen tareas de migración o refactorización hacia Java 21 en este repositorio, aplica las siguientes directrices técnicas:

1. **Namespaces Jakarta EE 10:**
   - Todo paquete legado `javax.persistence.*`, `javax.validation.*`, `javax.servlet.*` y `javax.annotation.*` debe migrarse a `jakarta.*`.

2. **Inmutabilidad con Java Records:**
   - Los DTOs (`CreditSummaryDto`, `BatchEvaluationResultDto`, `RuntimeTelemetryDto`) y objetos de valor o productos (`MortgageLoan`, `ConsumerLoan`, `SmeLoan`, `CorporateLineLoan`, `RiskAssessment`) deben modelarse como `record` inmutables.
   - Nota de compatibilidad con Jackson: Cuando un DTO se convierte en `record`, usa los nombres de componentes estándar (ej. `totalApplications()`) o mantén métodos de utilidad del dominio si son requeridos por la lógica de negocio.

3. **Diseño Orientado a Datos (Sealed Types + Pattern Matching):**
   - La interfaz `LoanProduct` debe declararse como `sealed interface LoanProduct permits MortgageLoan, ConsumerLoan, SmeLoan, CorporateLineLoan`.
   - En `RiskCalculatorService.evaluateProduct()`, utiliza expresiones `switch (product)` exhaustivas con **Pattern Matching** y guardas `when` en lugar de cadenas `if / else if (product instanceof ...)`.

4. **API de Fechas y Colecciones Secuenciadas:**
   - Prohibido el uso de `java.util.Date` y `java.text.SimpleDateFormat` en lógica de servicios; utiliza `java.time.LocalDateTime` y `java.time.format.DateTimeFormatter` (que es thread-safe).
   - Utiliza `Stream.toList()` en lugar de `Collectors.toList()`.
   - Utiliza los métodos de `SequencedCollection` de Java 21 (`getFirst()` y `getLast()`) en lugar de `get(0)` y `get(size() - 1)`.

5. **Plantillas Multilínea:**
   - Para payloads regulatorios JSON o reportes multilínea en `RegulatoryReportService`, utiliza **Text Blocks (`"""`)** con `.formatted(...)`.

6. **Concurrencia con Virtual Threads (Project Loom):**
   - Para operaciones con I/O bloqueante como `CreditBureauService`, utiliza `Executors.newVirtualThreadPerTaskExecutor()` en lugar de pools fijos de hilos de plataforma.
   - En Java 21, utiliza `thread.isVirtual()` directamente sin reflexión.

7. **Pruebas Unitarias con JUnit 5 (Jupiter):**
   - Todas las pruebas deben utilizar `@org.junit.jupiter.api.Test`, `@org.junit.jupiter.api.BeforeEach` y `org.junit.jupiter.api.Assertions.*`.
