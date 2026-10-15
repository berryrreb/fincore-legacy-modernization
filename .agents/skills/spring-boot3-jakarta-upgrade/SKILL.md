---
name: spring-boot3-jakarta-upgrade
description: >-
  Upgrades the FinCore project platform from Java 8, Spring Boot 2.7.18, javax.*, and JUnit 4 to Java 21 LTS, Spring Boot 3.3.5, jakarta.*, and JUnit 5 Jupiter. Use this skill when the user asks to upgrade the platform, pom.xml, Spring Boot 3, Jakarta EE namespaces, or JUnit tests (Mission 2).
---

# Skill: Upgrade de Plataforma a Java 21, Spring Boot 3.3 y Jakarta EE (Misión 2)

Usa esta skill cuando el usuario pida realizar el salto de plataforma hacia **Java 21 y Spring Boot 3.3.5** (Misión 2 del laboratorio).

## Pasos de Ejecución

### 1. Actualizar `pom.xml`
Modifica `pom.xml` para:
- Cambiar `<parent>` de `spring-boot-starter-parent` versión `2.7.18` a **`3.3.5`**.
- Actualizar `<version>` del proyecto a `2.0.0-JAVA21` y la descripción a Java 21 / Spring Boot 3.3.
- En `<properties>`, cambiar:
  ```xml
  <java.version>21</java.version>
  <maven.compiler.source>21</maven.compiler.source>
  <maven.compiler.target>21</maven.compiler.target>
  ```
- Eliminar las dependencias legadas de prueba `junit-vintage-engine` y `junit:junit`, dejando únicamente `spring-boot-starter-test` (que incluye JUnit 5 Jupiter).

### 2. Migrar paquetes `javax.*` a `jakarta.*`
Actualiza los imports en los siguientes 4 archivos:
- `src/main/java/com/fincore/risk/entity/CreditApplication.java`:
  - `javax.persistence.*` $\rightarrow$ `jakarta.persistence.*`
  - `javax.validation.constraints.*` $\rightarrow$ `jakarta.validation.constraints.*`
- `src/main/java/com/fincore/risk/dto/NewCreditRequestDto.java`:
  - `javax.validation.constraints.*` $\rightarrow$ `jakarta.validation.constraints.*`
- `src/main/java/com/fincore/risk/controller/CreditDashboardController.java`:
  - `javax.servlet.http.HttpServletRequest` $\rightarrow$ `jakarta.servlet.http.HttpServletRequest`
  - `javax.validation.Valid` $\rightarrow$ `jakarta.validation.Valid`
- `src/main/java/com/fincore/risk/config/DataInitializer.java`:
  - `javax.annotation.PostConstruct` $\rightarrow$ `jakarta.annotation.PostConstruct`

### 3. Migrar Tests Unitarios de JUnit 4 a JUnit 5 (Jupiter)
Actualiza las 2 clases de prueba en `src/test/java/com/fincore/risk/service/`:
- `RiskCalculatorServiceTest.java` y `RegulatoryReportServiceTest.java`:
  - Reemplazar `import org.junit.Before;` por `import org.junit.jupiter.api.BeforeEach;` y `@Before` por `@BeforeEach`.
  - Reemplazar `import org.junit.Test;` por `import org.junit.jupiter.api.Test;`.
  - Reemplazar `import static org.junit.Assert.*;` por `import static org.junit.jupiter.api.Assertions.*;`.
  - Nota de orden de parámetros en JUnit 5: `assertEquals(expected, actual, delta)` mantiene el mismo orden para `double` sin mensaje.

### 4. Validar Compilación y Pruebas con Java 21
Ejecuta la verificación en terminal asegurándote de usar JDK 21:
```bash
source ./scripts/use-java.sh 21 && mvn clean test
```
Confirma al usuario que todos los tests pasan en verde y recuérdale que puede levantar `mvn spring-boot:run` para ver cómo el banner superior del portal cambia automáticamente de **Ámbar (Java 8)** a **Verde Esmeralda (Java 21 LTS + Spring Boot 3.3.5)**.
