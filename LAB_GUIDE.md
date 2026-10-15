# Guía Práctica del Laboratorio: Modernización de Java 8 a Java 21 con Antigravity

**Duración estimada:** 90 a 120 minutos  
**Caso de Negocio:** *FinCore — Motor Bancario de Riesgo y Originación de Crédito*

---

## Agenda del Laboratorio

| Misión | Tiempo | Objetivo Principal | Validación Visual en Web (`localhost:8080`) |
| :--- | :--- | :--- | :--- |
| **Misión 1** | 15 min | Diagnóstico del sistema legado en Java 8 y Plan de Migración con Antigravity | Banner en **Ámbar (Java 8)** y Buró Masivo tardando **~1,350 ms** |
| **Misión 2** | 30 min | Upgrade de Plataforma: `pom.xml`, Spring Boot 3.3, `javax.*` $\rightarrow$ `jakarta.*` y JUnit 4 $\rightarrow$ 5 | Banner cambia automáticamente a **Verde (Java 21 LTS + Spring Boot 3.3)** |
| **Misión 3** | 30 min | Refactorización Idiomática a Java 21: `Sealed Interfaces`, `Records`, `Pattern Matching switch`, `Text Blocks` y `java.time` | Originar nuevo crédito y abrir el modal de **Dictamen Regulatorio JSON** |
| **Misión 4** | 15 min | Concurrencia Moderna con **Virtual Threads (Project Loom)** | Botón **Consultar Buró Masivo** baja de **~1,350 ms a ~230 ms (~6x más rápido)** |

---

## Misión 1: Línea Base en Java 8 y Planificación con Antigravity (15 min)

### Paso 1.1 — Verificar y levantar la aplicación en Java 8
1. Verifica en tu terminal que estés usando **Java 8**:
   ```bash
   java -version
   ```
2. Ejecuta los tests y levanta la aplicación:
   ```bash
   mvn test
   mvn spring-boot:run
   ```
3. Abre **[http://localhost:8080](http://localhost:8080)** en tu navegador y observa:
   * El banner superior aparece en **Ámbar**: `MODO LEGADO: JAVA 8 DETECTADO (1.8.0) | Spring Boot 2.7.18`.
   * Haz clic en el botón azul **"Consultar Buró Masivo (12 Créditos)"**.
   * Observa que tarda **~1,350 ms** porque `CreditBureauService` utiliza un `FixedThreadPool(2)` que bloquea los hilos del sistema operativo en tandas de 2 en 2.
   * Haz clic en el botón **"Dictamen"** de cualquier solicitud para ver el expediente regulatorio Basilea III.
4. Detén la aplicación en la terminal (`Ctrl + C`).

### Paso 1.2 — Auditar el repositorio con Antigravity
Usa el comando `/plan` o pega el siguiente prompt en el chat de **Antigravity**:

> **Prompt para Antigravity (Misión 1):**
> ```text
> Analiza este repositorio bancario (FinCore) que actualmente corre en Java 8 y Spring Boot 2.7.
> Identifica toda la deuda técnica que debemos migrar para llevarlo a Java 21 LTS y Spring Boot 3.3, agrupándola en:
> 1. Configuración de Maven (pom.xml), dependencias y pruebas unitarias (JUnit 4 a JUnit 5).
> 2. Cambio de paquetes de javax.* a jakarta.*.
> 3. Oportunidades para aplicar características modernas de Java 21 (Records, Sealed Interfaces, Pattern Matching for switch con when, Text Blocks, Sequenced Collections y java.time).
> 4. Modernización del modelo de concurrencia en CreditBureauService usando Virtual Threads.
> No modifiques archivos todavía; entrégame únicamente el diagnóstico y el plan de ataque.
> ```

---

## Misión 2: El Salto de Plataforma — Java 21, Spring Boot 3.3 y `jakarta.*` (30 min)

En esta misión haremos que el proyecto compile y pase todas sus pruebas sobre **Java 21 y Spring Boot 3.3**, resolviendo el dolor más frecuente en la banca: la transición de `javax.*` a `jakarta.*` y la actualización de JUnit 4 a JUnit 5.

### Paso 2.1 — Activar JDK 21 en tu entorno
Cambia tu terminal a **Java 21**:
```bash
# Ejemplo con SDKMAN:
sdk use java 21.0.4-tem

# O ejemplo en macOS:
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
java -version
```

### Paso 2.2 — Ejecutar la migración de plataforma con Antigravity
Pide a Antigravity que ejecute la actualización estructural y verifique con `mvn test`:

> **Prompt para Antigravity (Misión 2):**
> ```text
> Ejecuta la Fase de Upgrade de Plataforma a Java 21 y Spring Boot 3.3.5:
> 1. Actualiza el pom.xml a spring-boot-starter-parent 3.3.5 y configura java.version / maven.compiler.source / target en 21. Elimina junit-vintage-engine y junit 4 para usar JUnit 5 Jupiter incluido en spring-boot-starter-test.
> 2. Migra todos los imports de javax.persistence.*, javax.validation.*, javax.servlet.* y javax.annotation.* a sus equivalentes en jakarta.* en todas las entidades, DTOs, controladores y configuración.
> 3. Migra las clases de prueba en src/test/java de JUnit 4 (@Before, @org.junit.Test, org.junit.Assert) a JUnit 5 (@BeforeEach, @org.junit.jupiter.api.Test, org.junit.jupiter.api.Assertions).
> 4. Ejecuta 'mvn clean test' en la terminal para verificar que todo compile y las pruebas pasen en verde.
> ```

### Paso 2.3 — Validación Gráfica en el Navegador
1. Levanta nuevamente la aplicación con Java 21:
   ```bash
   mvn spring-boot:run
   ```
2. Recarga **[http://localhost:8080](http://localhost:8080)**:
   * ¡El banner superior ahora brilla en **Verde Esmeralda**: `MODERNIZADO: JAVA 21 LTS ACTIVO | Spring Boot 3.3.5`!
   * Nota que el indicador derecho aún dice `OS Platform Thread Pool (Bloqueante)` porque todavía no hemos modernizado el motor de concurrencia ni la sintaxis interna.
3. Detén la aplicación (`Ctrl + C`) para pasar a la Misión 3.

---

## Misión 3: Refactorización Idiomática a Java 21 (30 min)

Ahora eliminaremos cientos de líneas de código legado en el motor de riesgo bancario aplicando las mejores características del lenguaje **Java 21**.

### Qué vamos a transformar:
1. **`LoanProduct` y sus 4 subclases (`MortgageLoan`, `ConsumerLoan`, `SmeLoan`, `CorporateLineLoan`):**
   * Convertir `LoanProduct` en una **`sealed interface`** y las 4 implementaciones en **`record`** inmutables.
2. **`RiskCalculatorService`:**
   * Reemplazar la cadena de `if / else if (product instanceof ...)` y los castings manuales por un **`switch (product)` exhaustivo con Pattern Matching y guardas `when`**.
   * Reemplazar `SimpleDateFormat` y `java.util.Date` por `DateTimeFormatter` y `LocalDateTime`.
   * Reemplazar `Collectors.toList()` por `.toList()`, y `sortedApps.get(0)` / `sortedApps.get(sortedApps.size() - 1)` por **`sortedApps.getFirst()`** y **`sortedApps.getLast()`** (`SequencedCollection` de Java 21).
3. **DTOs (`CreditSummaryDto`, `BatchEvaluationResultDto`, `RuntimeTelemetryDto`) y `RegulatoryReportService`:**
   * Convertir los DTOs verbosos en **`record`**.
   * Reemplazar la concatenación de strings en `RegulatoryReportService` por un **Text Block (`"""`)** con `.formatted(...)`.

### Paso 3.1 — Pedir a Antigravity la refactorización a Java 21
> **Prompt para Antigravity (Misión 3):**
> ```text
> Refactoriza el código de negocio de FinCore para adoptar código idiomático de Java 21:
> 1. En el paquete com.fincore.risk.model, convierte LoanProduct en una 'sealed interface' que permita únicamente MortgageLoan, ConsumerLoan, SmeLoan y CorporateLineLoan, y convierte esas 4 clases además de RiskAssessment en 'record' de Java 21 (conservando sus métodos de cálculo de ratios como getLoanToValueRatio(), getDebtToIncomeRatio(), etc.).
> 2. En RiskCalculatorService:
>    - Reemplaza la cadena de 'if (product instanceof ...)' en evaluateProduct() por una expresión 'switch (product)' exhaustiva usando Pattern Matching y guardas 'when' de Java 21.
>    - Reemplaza SimpleDateFormat y java.util.Date por DateTimeFormatter y LocalDateTime.
>    - En buildPortfolioSummary(), usa Stream.toList() y los nuevos métodos de SequencedCollection de Java 21: getFirst() y getLast().
> 3. Convierte CreditSummaryDto, BatchEvaluationResultDto y RuntimeTelemetryDto en 'record' de Java 21.
> 4. En RegulatoryReportService, reemplaza la concatenación de strings del JSON por un Text Block (""") con .formatted() y usa LocalDateTime.
> 5. Actualiza las llamadas necesarias en servicios, controladores y tests unitarios, y ejecuta 'mvn test' hasta que todas las pruebas pasen.
> ```

### Paso 3.2 — Validación Gráfica en el Navegador
1. Ejecuta `mvn spring-boot:run` y abre **[http://localhost:8080](http://localhost:8080)**.
2. En el formulario derecho **"Originar Nueva Solicitud de Crédito"**, crea un nuevo crédito (por ejemplo, para *"Grupo Financiero Atlas"*, segmento `CORPORATE`, Score `790`, Monto `950000`).
3. Verifica que aparezca al final de la tabla con su tasa calculada por el nuevo `switch` con Pattern Matching, y que la tarjeta **"Extremos de Cola (Sequenced)"** muestre el nuevo folio en `Último` gracias a `getLast()`.
4. Haz clic en **"Dictamen"** sobre la nueva solicitud y verifica que el modal muestre el JSON generado limpiamente con **Text Blocks (`"""`)**.

---

## Misión 4: El Efecto "Wow" — Concurrencia con Virtual Threads (15 min)

En el cierre del laboratorio resolveremos el cuello de botella del servicio que consulta al Buró Nacional de Crédito (`CreditBureauService`).

### Paso 4.1 — Migrar a Virtual Threads con Antigravity
> **Prompt para Antigravity (Misión 4):**
> ```text
> Moderniza CreditBureauService para utilizar Virtual Threads de Java 21 (Project Loom):
> 1. Reemplaza Executors.newFixedThreadPool(2) por Executors.newVirtualThreadPerTaskExecutor().
> 2. Simplifica la creación de tareas Callable con lambdas modernas y reemplaza Thread.isVirtual() por llamada directa de Java 21 en lugar de reflexión.
> 3. Ejecuta 'mvn test' para validar que todo compile y funcione correctamente.
> ```

### Paso 4.2 — Prueba de Fuego Visual en el Dashboard Web
1. Reinicia la aplicación (`mvn spring-boot:run`) y recarga **[http://localhost:8080](http://localhost:8080)**.
2. Observa en el banner superior que el indicador derecho ahora marca en azul/verde:  
   **`Virtual Threads (Project Loom) — Virtual Threads (Loom Unbounded)`**.
3. Haz clic en el botón **"Consultar Buró Masivo (12 Créditos)"**:
   * **Antes (Java 8 Platform Threads):** `~1,350 ms`
   * **Ahora (Java 21 Virtual Threads):** **`~225 – 250 ms` (`~6x MÁS RÁPIDO`)**
   * Observa los badges de hilos ejecutores mostrando `VirtualThread[#...]` y haz clic en **"Actualizar Tabla"** para ver todas las filas procesadas por hilos virtuales.

---

## ¡Felicidades!
Has completado la modernización integral de una aplicación bancaria de **Java 8 a Java 21 LTS** utilizando **Google Antigravity**, validando tanto la calidad interna del código y sus pruebas como el comportamiento gráfico y el rendimiento en producción.
