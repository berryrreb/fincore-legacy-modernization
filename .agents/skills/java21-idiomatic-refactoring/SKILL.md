---
name: java21-idiomatic-refactoring
description: >-
  Refactors FinCore legacy Java 8 business logic, domain models, and DTOs into idiomatic Java 21 using Sealed Interfaces, Records, Pattern Matching for switch with when guards, Text Blocks, Sequenced Collections (getFirst/getLast), Stream.toList(), and java.time. Use this skill when the user asks to refactor the code, models, DTOs, or risk engine to Java 21 features (Mission 3).
---

# Skill: Refactorización Idiomática a Java 21 (Misión 3)

Usa esta skill cuando el usuario pida refactorizar el dominio bancario, los DTOs y los servicios hacia código idiomático de **Java 21** (Misión 3 del laboratorio).

## Pasos de Ejecución

### 1. Jerarquía Sellada (`sealed interface`) y `record` en `com.fincore.risk.model`
1. **`LoanProduct.java`**:
   - Declárala como:
     ```java
     public sealed interface LoanProduct permits MortgageLoan, ConsumerLoan, SmeLoan, CorporateLineLoan {
         String getProductCode();
         double requestedAmount();
         int termMonths();
         int creditScore();
     }
     ```
2. **`MortgageLoan.java`**, **`ConsumerLoan.java`**, **`SmeLoan.java`** y **`CorporateLineLoan.java`**:
   - Conviértelos en `record` que implementen `LoanProduct`.
   - Conserva sus métodos de cálculo de negocio (`getLoanToValueRatio()` en `MortgageLoan`, `getDebtToIncomeRatio()` en `ConsumerLoan`, `getCollateralCoverageRatio()` en `SmeLoan`, `hasStrongCollateralBacking()` en `CorporateLineLoan`, y `getProductCode()`).
3. **`RiskAssessment.java`**:
   - Conviértelo en `record`:
     ```java
     public record RiskAssessment(
             String riskLevel,
             double annualInterestRate,
             String recommendedStatus,
             String rationale,
             String evaluatedTimestamp
     ) {}
     ```
   - Importante: Actualiza las llamadas a `RiskAssessment` en `DataInitializer`, `CreditDashboardController`, `CreditBureauService`, `RegulatoryReportService` y `RiskCalculatorServiceTest` para usar los componentes del record (`riskLevel()`, `annualInterestRate()`, `recommendedStatus()`, `rationale()`, `evaluatedTimestamp()`) o incluye métodos delegados en el record para mantener compatibilidad total.

### 2. Convertir DTOs en `record` en `com.fincore.risk.dto`
Convierte en `record` inmutables:
- **`CreditSummaryDto.java`**
- **`BatchEvaluationResultDto.java`**
- **`RuntimeTelemetryDto.java`**
> Nota: Conserva `NewCreditRequestDto.java` como clase con getters/setters y constructor vacío porque Spring MVC usa data-binding mutable en `@ModelAttribute("newCredit")` para el formulario HTML. Actualiza en `RiskCalculatorServiceTest` las llamadas a `summary.totalApplications()`, `summary.approvedCount()`, `summary.rejectedCount()`, `summary.oldestReferenceCode()`, `summary.newestReferenceCode()`.

### 3. Refactorizar `RiskCalculatorService.java`
1. **Fechas Thread-Safe (`java.time`):**
   - Elimina `SimpleDateFormat` y `java.util.Date`. Usa `private static final DateTimeFormatter MODERN_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");` y `LocalDateTime.now().format(MODERN_DATE_FORMAT)`.
2. **Pattern Matching para `switch` en `mapToLoanProduct()` y `evaluateProduct()`:**
   - En `evaluateProduct(LoanProduct product)`, reemplaza toda la cadena de `if / else if (product instanceof ...)` por un `return switch (product)` exhaustivo con **Pattern Matching y guardas `when`**:
     ```java
     return switch (product) {
         case MortgageLoan m when m.creditScore() >= 740 && m.getLoanToValueRatio() <= 0.80 ->
                 new RiskAssessment("LOW", 7.25, "APPROVED",
                         "Hipotecario Preferencial: Score excelente y LTV <= 80% (" + Math.round(m.getLoanToValueRatio() * 100) + "%)", timestamp);
         case MortgageLoan m when m.creditScore() >= 660 && m.getLoanToValueRatio() <= 0.90 ->
                 new RiskAssessment("MEDIUM", 9.10, "APPROVED",
                         "Hipotecario Estándar: Garantía inmobiliaria aceptable (" + Math.round(m.getLoanToValueRatio() * 100) + "% LTV)", timestamp);
         case MortgageLoan m ->
                 new RiskAssessment("HIGH", 11.85, "CONDITIONAL",
                         "Hipotecario de Alto Riesgo: Requiere aval adicional o mayor enganche", timestamp);

         case ConsumerLoan c when c.creditScore() >= 720 && c.getDebtToIncomeRatio() <= 0.35 ->
                 new RiskAssessment("LOW", 14.50, "APPROVED",
                         "Consumo Prime: Capacidad de pago saludable (DTI " + Math.round(c.getDebtToIncomeRatio() * 100) + "%)", timestamp);
         case ConsumerLoan c when c.creditScore() >= 620 && c.getDebtToIncomeRatio() <= 0.50 ->
                 new RiskAssessment("MEDIUM", 19.90, "CONDITIONAL",
                         "Consumo Sub-Prime: Endeudamiento moderado, sujeto a validación de buró", timestamp);
         case ConsumerLoan c ->
                 new RiskAssessment("CRITICAL", 28.50, "REJECTED",
                         "Consumo Rechazado: Score insuficiente (" + c.creditScore() + ") o sobreendeudamiento", timestamp);

         case SmeLoan s when s.creditScore() >= 700 && s.getCollateralCoverageRatio() >= 1.0 ->
                 new RiskAssessment("LOW", 10.75, "APPROVED",
                         "PyME Crecimiento: Garantía líquida/hipotecaria cubre >= 100% del crédito", timestamp);
         case SmeLoan s when s.creditScore() >= 640 && s.annualRevenue() >= s.requestedAmount() * 1.5 ->
                 new RiskAssessment("MEDIUM", 13.40, "APPROVED",
                         "PyME Capital de Trabajo: Flujo anual respalda la operación comercial", timestamp);
         case SmeLoan s ->
                 new RiskAssessment("HIGH", 17.20, "CONDITIONAL",
                         "PyME Observada: Requiere garantía solidaria de socios principales", timestamp);

         case CorporateLineLoan corp when corp.creditScore() >= 750 && corp.hasStrongCollateralBacking() ->
                 new RiskAssessment("LOW", 6.80, "APPROVED",
                         "Línea Corporativa AAA: Colateral institucional fuerte y EBITDA sólido", timestamp);
         case CorporateLineLoan corp when corp.creditScore() >= 680 ->
                 new RiskAssessment("MEDIUM", 8.95, "CONDITIONAL",
                         "Línea Corporativa Sindicada: Requiere aprobación de Comité de Riesgo Mayor", timestamp);
         case CorporateLineLoan corp ->
                 new RiskAssessment("CRITICAL", 14.00, "REJECTED",
                         "Línea Corporativa Rechazada: Rating crediticio corporativo bajo el umbral mínimo", timestamp);
     };
     ```
3. **`Stream.toList()` y `SequencedCollection` en `buildPortfolioSummary()`:**
   - Reemplaza `.collect(Collectors.toList())` por `.toList()`.
   - Reemplaza `sortedApps.get(0).getReferenceCode()` por **`sortedApps.getFirst().getReferenceCode()`** y `sortedApps.get(sortedApps.size() - 1).getReferenceCode()` por **`sortedApps.getLast().getReferenceCode()`**.

### 4. Text Blocks (`"""`) en `RegulatoryReportService.java`
Reemplaza `SimpleDateFormat` por `DateTimeFormatter.ISO_LOCAL_DATE_TIME` con `LocalDateTime.now().withNano(0)` y sustituye la concatenación de strings JSON por un **Text Block (`"""`)** con `.formatted(...)` manteniendo exactamente los mismos nombres de campos JSON (`regulatoryStandard`, `referenceCode`, `committeeDecision`, etc.) para que los tests y el modal web funcionen intactos.

### 5. Verificación
Ejecuta `source ./scripts/use-java.sh 21 && mvn test` y verifica que todos los tests pasen sin errores.
