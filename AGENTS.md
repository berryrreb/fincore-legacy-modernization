# FinCore — Guía de Contexto y Reglas para Agentes (AGENTS.md)

Este repositorio contiene **FinCore Risk & Credit Origination Engine**, una aplicación bancaria diseñada para el laboratorio práctico de modernización de **Java 8 + Spring Boot 2.7 + Google App Engine (`app.yaml`)** hacia **Java 21 LTS + Spring Boot 3.3 + Virtual Threads + Google Cloud Run** ejecutado en **Google Cloud Shell (Qwiklabs)**.

## Mapa de Misiones del Laboratorio y Skills Disponibles

El repositorio cuenta con **5 Skills especializadas** en `.agents/skills/` para guiar cada etapa sin requerir prompts extensos. Cuando el usuario solicite realizar alguna de estas fases, lee y sigue las instrucciones de la skill correspondiente:

1. **Misión 1 — Diagnóstico y Plan de Modernización (`java8-legacy-assessment`):**
   - Ruta: `.agents/skills/java8-legacy-assessment/SKILL.md`
   - Cuándo usarla: Cuando el usuario pida analizar, auditar o diagnosticar la deuda técnica del repositorio legado y generar el plan de migración hacia Java 21 y Cloud Run.
   - Regla clave: En la Misión 1 **no modifiques ningún archivo**; entrega únicamente el diagnóstico estructurado y el plan de acción.

2. **Misión 2 — Upgrade de Plataforma a Java 21, Spring Boot 3.3, Jakarta EE y JUnit 5 (`spring-boot3-jakarta-upgrade`):**
   - Ruta: `.agents/skills/spring-boot3-jakarta-upgrade/SKILL.md`
   - Cuándo usarla: Cuando el usuario pida actualizar `pom.xml`, subir a Java 21 y Spring Boot 3.3.5, migrar paquetes `javax.*` a `jakarta.*` o migrar los tests de JUnit 4 a JUnit 5.

3. **Misión 3 — Refactorización Idiomática a Java 21 (`java21-idiomatic-refactoring`):**
   - Ruta: `.agents/skills/java21-idiomatic-refactoring/SKILL.md`
   - Cuándo usarla: Cuando el usuario pida refactorizar los modelos, DTOs, el motor de riesgo (`RiskCalculatorService`) o el generador de reportes (`RegulatoryReportService`) usando `sealed interface`, `record`, Pattern Matching for `switch` con guardas `when`, `Text Blocks`, `SequencedCollection` (`getFirst()` / `getLast()`), `Stream.toList()` y `java.time`.

4. **Misión 4 — Concurrencia con Virtual Threads / Project Loom (`java21-virtual-threads`):**
   - Ruta: `.agents/skills/java21-virtual-threads/SKILL.md`
   - Cuándo usarla: Cuando el usuario pida modernizar `CreditBureauService` para eliminar el cuello de botella del `FixedThreadPool(2)` y activar `Executors.newVirtualThreadPerTaskExecutor()`.

5. **Misión 5 — Migración de App Engine a Google Cloud Run (`appengine-to-cloudrun-migration`):**
   - Ruta: `.agents/skills/appengine-to-cloudrun-migration/SKILL.md`
   - Cuándo usarla: Cuando el usuario pida migrar desde `app.yaml` (App Engine), crear el `Dockerfile` para Java 21, contenedorizar la aplicación o desplegarla en **Google Cloud Run**.

---

## Directrices Técnicas Globales del Proyecto

1. **Compatibilidad con la Interfaz Web (`src/main/resources/templates/index.html`):**
   - No elimines ni renombres los atributos del `Model` en `CreditDashboardController.showDashboard()` (`applications`, `summary`, `telemetry`, `newCredit`, `cloudRunActive`, `cloudRunService`, `cloudRunRevision`).
   - En `CreditDashboardController`, conserva `objectMapper.convertValue(summaryDto, Map.class)` y `objectMapper.convertValue(telemetryDto, Map.class)` para que Thymeleaf funcione tanto con POJOs como con `record`s de Java 21.
   - Cuando conviertas `RiskAssessment`, `LoanProduct` y los DTOs en `record`, actualiza las llamadas en `DataInitializer`, `CreditDashboardController`, `RiskCalculatorService`, `CreditBureauService`, `RegulatoryReportService` y las clases de prueba (`RiskCalculatorServiceTest`, `RegulatoryReportServiceTest`) para usar los métodos accesores del `record` (ej. `assessment.riskLevel()`, `summary.totalApplications()`) o conserva métodos delegados de compatibilidad.

2. **Entorno de Ejecución (Google Cloud Shell & Cloud Run):**
   - Para cambiar entre Java 8 y Java 21 en la terminal, existe el script `./scripts/use-java.sh` (`source ./scripts/use-java.sh 8` o `source ./scripts/use-java.sh 21`).
   - La aplicación escucha en `server.port=${PORT:8080}` y tiene `server.forward-headers-strategy=framework` para funcionar detrás del proxy HTTPS de Cloud Shell Web Preview y del balanceador de Google Cloud Run.
   - Al construir el `Dockerfile` para Cloud Run, utiliza una construcción **multi-stage** con imágenes oficiales `eclipse-temurin:21-jdk-jammy` (build) y `eclipse-temurin:21-jre-jammy` (runtime), usuario no-root y flags de contenedor (`-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0`).
