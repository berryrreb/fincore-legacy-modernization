---
name: appengine-to-cloudrun-migration
description: >-
  Migrates FinCore from legacy Google App Engine (app.yaml) to Google Cloud Run. Generates a production-grade multi-stage Java 21 Dockerfile and .dockerignore, verifies Cloud Run PORT and proxy settings, explains the architectural mapping from App Engine to Cloud Run, and deploys the service using gcloud run deploy. Use this skill when the user asks to containerize, migrate from App Engine, or deploy the application to Google Cloud Run (Mission 5).
---

# Skill: Migración de Google App Engine a Google Cloud Run (Misión 5)

Usa esta skill cuando el usuario solicite migrar la aplicación desde el esquema legado de **Google App Engine (`app.yaml`)** y desplegarla en **Google Cloud Run**.

## Pasos de Ejecución

### 1. Explicar el Mapeo Arquitectónico (App Engine $\rightarrow$ Cloud Run)
Inspecciona el archivo legado `app.yaml` del repositorio y presenta una breve tabla comparativa para el equipo bancario:
- **`runtime: java8` (App Engine)** $\rightarrow$ **Contenedor OCI Multi-Stage con `eclipse-temurin:21-jre-jammy` (Cloud Run)**: Control total del runtime JRE 21 LTS y parches de seguridad.
- **`instance_class: F4` (`512MB`)** $\rightarrow$ **`--cpu 1 --memory 1Gi` con `-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0`**: Dimensionamiento explícito de CPU/RAM adaptado a contenedores.
- **`max_concurrent_requests: 10` (App Engine)** $\rightarrow$ **`--concurrency 80` en Cloud Run + Java 21 Virtual Threads**: Gracias a Project Loom, una sola instancia de Cloud Run puede atender hasta 80 peticiones concurrentes con I/O bloqueante sin agotar hilos del sistema operativo, reduciendo drásticamente el costo de infraestructura.
- **Escalado propietario en `app.yaml`** $\rightarrow$ **`--min-instances 0 --max-instances 5`**: Escalado automático en segundos y scale-to-zero en entornos no productivos.

### 2. Crear `.dockerignore` en la raíz del proyecto
Crea un archivo `.dockerignore` limpio para acelerar el envío de contexto a Cloud Build:
```text
target/
.git/
.gitignore
.agents/
docs/
.idea/
.vscode/
*.md
app.yaml
```

### 3. Crear `Dockerfile` Multi-Stage para Java 21 en la raíz del proyecto
Crea el archivo `Dockerfile` en la raíz del repositorio con las mejores prácticas de producción para **Java 21 + Spring Boot 3 en Cloud Run**:
```dockerfile
# =============================================================================
# Stage 1: Build Stage (Maven + Eclipse Temurin JDK 21)
# =============================================================================
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Cache dependencies first
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compile and package executable Spring Boot JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# =============================================================================
# Stage 2: Production Runtime Stage (Minimal Eclipse Temurin JRE 21)
# =============================================================================
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create non-root banking security user
RUN groupadd -r fincore && useradd -r -g fincore fincore

# Copy compiled artifact from builder stage
COPY --from=builder /workspace/target/*.jar /app/app.jar
RUN chown -R fincore:fincore /app

USER fincore

# Cloud Run injects PORT (default 8080)
ENV PORT=8080
EXPOSE 8080

# Container-aware JVM flags optimized for Cloud Run & Java 21
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
```

### 4. Verificar Configuración de `application.properties`
Verifica que `src/main/resources/application.properties` tenga:
- `server.port=${PORT:8080}`
- `server.forward-headers-strategy=framework`

### 5. Desplegar a Google Cloud Run desde Cloud Shell
Ejecuta los siguientes comandos en la terminal de Cloud Shell (o guíalos paso a paso si se requiere confirmación):

1. Verificar el proyecto activo en Google Cloud (Qwiklabs):
   ```bash
   gcloud config get-value project
   ```
2. Habilitar las APIs necesarias de Cloud Run, Cloud Build y Artifact Registry:
   ```bash
   gcloud services enable run.googleapis.com cloudbuild.googleapis.com artifactregistry.googleapis.com
   ```
3. Desplegar el servicio en **Cloud Run** construyendo desde el código fuente (`--source .`):
   ```bash
   gcloud run deploy fincore-risk-engine \
     --source . \
     --region us-central1 \
     --allow-unauthenticated \
     --memory 1Gi \
     --cpu 1 \
     --concurrency 80 \
     --max-instances 5 \
     --set-env-vars="FINCORE_REGULATORY_STANDARD=BASEL-III-CNBV-2026"
   ```

### 6. Validación Post-Despliegue
Una vez finalizado el comando `gcloud run deploy`, entrega al usuario la **Service URL (`https://fincore-risk-engine-...run.app`)** e indícale que al abrirla en su navegador verá:
1. En la barra superior derecha, el nuevo badge verde **`CLOUD RUN ACTIVO: fincore-risk-engine`** (detectado automáticamente mediante las variables `K_SERVICE` y `K_REVISION` de Cloud Run).
2. El banner principal en **Verde Esmeralda (`MODERNIZADO: JAVA 21 LTS ACTIVO`)**.
3. El benchmark del Buró de Crédito ejecutándose en **~230 ms** sobre **Virtual Threads** directamente en Google Cloud Run.
