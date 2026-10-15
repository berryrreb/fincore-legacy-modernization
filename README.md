# FinCore | Modernización Bancaria de Java 8 a Java 21 LTS con Antigravity

Bienvenido al repositorio oficial del laboratorio práctico **FinCore Risk & Credit Origination Engine**.

Este proyecto simula el motor central de evaluación de riesgo crediticio y originación de préstamos de una institución financiera. La aplicación cuenta con un **Dashboard Web Interactivo** que permite validar gráficamente el funcionamiento del sistema, medir el rendimiento de llamadas I/O al Buró de Crédito y verificar en vivo la versión del runtime (**Java 8 Legacy** vs **Java 21 LTS + Spring Boot 3**).

---

## Objetivo del Laboratorio (Duración: 1h 30m – 2h 00m)

Modernizar una aplicación bancaria legada construida en **Java 8 + Spring Boot 2.7 (`javax.*` + JUnit 4)** hacia **Java 21 LTS + Spring Boot 3.3 (`jakarta.*` + JUnit 5 + Virtual Threads)** utilizando **Google Antigravity**, validando cada etapa tanto con pruebas unitarias como visualmente en el portal web.

---

## Qué incluye la Aplicación Web (`http://localhost:8080`)

1. **Banner de Telemetría Automática en Vivo:**
   * Detecta automáticamente la versión de Java (`1.8` vs `21`), la versión de Spring Boot (`2.7` vs `3.3`) y el motor de concurrencia activo (`OS Platform Threads` vs `Java 21 Virtual Threads`).
   * En **Java 8** se muestra en color **Ámbar (`MODO LEGADO`)**; al completar el upgrade a **Java 21**, cambia automáticamente a **Verde (`MODERNIZADO: JAVA 21 LTS ACTIVO`)** sin tocar una sola línea de HTML.
2. **Simulador de Verificación Masiva en Buró Nacional de Crédito (Benchmark Visual):**
   * Ejecuta la validación de las 12 solicitudes del portafolio contra un servicio externo simulado con **220 ms de latencia de red I/O** por operación.
   * **En Java 8 (`FixedThreadPool(2)`):** Tarda **~1,350 ms** debido al cuello de botella de hilos del sistema operativo.
   * **En Java 21 (`Virtual Threads` - Project Loom):** Cae a **~230 ms (~6x más rápido)** ejecutando todas las consultas bloqueantes en paralelo sobre hilos virtuales ligeros.
3. **Bandeja de Solicitudes y Formulario de Originación de Crédito:**
   * Evalúa 4 segmentos bancarios (`MORTGAGE`, `CONSUMER`, `SME`, `CORPORATE`), calculando tasa anual (T.E.A.), nivel de riesgo (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`) y dictamen del comité.
4. **Visor de Expediente Regulatorio Basilea III (JSON):**
   * Genera en un modal el dictamen técnico regulatorio de cada crédito.

---

## Mapa de Deuda Técnica a Modernizar en el Laboratorio

| Componente | Estado Inicial (Java 8 Legacy) | Estado Objetivo (Java 21 LTS) |
| :--- | :--- | :--- |
| **Build & Plataforma** (`pom.xml`) | Java `1.8`, Spring Boot `2.7.18`, JUnit 4 Vintage | Java `21`, Spring Boot `3.3.5`, JUnit 5 Jupiter |
| **Namespaces JEE** (`CreditApplication`, `CreditDashboardController`, `DataInitializer`) | Paquetes `javax.persistence.*`, `javax.validation.*`, `javax.servlet.*`, `javax.annotation.*` | Migración completa a **`jakarta.*`** |
| **Jerarquía de Productos** (`LoanProduct` + 4 subclases) | Interfaz abierta + 4 POJOs verbosos (~80 líneas cada uno) | **`sealed interface LoanProduct permits ...`** + 4 **`record`** compactos |
| **Motor de Reglas** (`RiskCalculatorService`) | Escalera de `if / else if (product instanceof ...)` con castings manuales, `SimpleDateFormat` no thread-safe, `Collectors.toList()` y `list.get(0)` | **Pattern Matching para `switch`** con guardas (`when`), `DateTimeFormatter` + `LocalDateTime`, `Stream.toList()` y `SequencedCollection` (`getFirst()` / `getLast()`) |
| **DTOs y Reportes** (`CreditSummaryDto`, `BatchEvaluationResultDto`, `RuntimeTelemetryDto`, `RegulatoryReportService`) | POJOs de 100+ líneas y concatenación manual de JSON con `+ "\n" +` | **`record`** inmutables y **Text Blocks (`"""`)** con `.formatted(...)` |
| **Concurrencia I/O** (`CreditBureauService`) | `Executors.newFixedThreadPool(2)` (~1,350 ms por lote) | **`Executors.newVirtualThreadPerTaskExecutor()`** (~230 ms por lote) |

---

## Prerrequisitos en tu Máquina

1. **JDK 8** (para levantar la línea base legada) y **JDK 21** (para realizar la migración).
   * *Tip con SDKMAN!:*
     ```bash
     sdk install java 8.0.422-tem
     sdk install java 21.0.4-tem
     ```
   * *Tip en macOS con `/usr/libexec/java_home`:*
     ```bash
     # Cambiar a Java 8
     export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
     # Cambiar a Java 21
     export JAVA_HOME=$(/usr/libexec/java_home -v 21)
     ```
2. **Apache Maven 3.8+** (`mvn -version`).
3. **Google Antigravity** abierto en la carpeta de este repositorio.

---

## Inicio Rápido

1. Asegúrate de tener activo **Java 8** en tu terminal (`java -version`).
2. Ejecuta las pruebas unitarias legadas:
   ```bash
   mvn clean test
   ```
3. Levanta la aplicación web:
   ```bash
   mvn spring-boot:run
   ```
4. Abre en tu navegador: **[http://localhost:8080](http://localhost:8080)**
5. Sigue paso a paso las instrucciones del taller en **[LAB_GUIDE.md](./LAB_GUIDE.md)**.
