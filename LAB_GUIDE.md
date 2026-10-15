# Guía Rápida del Instructor y Participante — Laboratorio FinCore (Qwiklabs & Cloud Shell)

> **Nota:** Todas las instrucciones completas paso a paso se encuentran en **[`README.md`](./README.md)** para que los asistentes las visualicen automáticamente apenas abran el repositorio en GitHub o en Google Cloud Shell Editor. Este archivo sirve como hoja de referencia rápida (*Cheat Sheet*) de comandos y prompts por misión.

---

## Resumen de Prompts Cortos (usando las Skills de `.agents/skills/`)

Gracias a [`AGENTS.md`](./AGENTS.md) y a las 5 Skills preconfiguradas en [`.agents/skills/`](./.agents/skills/), los asistentes pueden ejecutar cada misión con una sola oración:

### Paso 0 — Preparación y Setup de Antigravity CLI en Cloud Shell
```bash
# 1. Verificar proyecto de Qwiklabs y habilitar APIs de Agent Platform
gcloud config get-value project
gcloud services enable aiplatform.googleapis.com cloudaicompanion.googleapis.com

# 2. Clonar el repositorio
git clone https://github.com/berryrreb/fincore-legacy-modernization.git
cd fincore-legacy-modernization

# 3. Actualizar Antigravity CLI e iniciar sesión con el proyecto de GCP
sudo agy update
agy
# En el menú interactivo de agy:
# -> Select login method: 2. Use a Google Cloud project
# -> Select Google Cloud sign-in method: 1. Continue with Google Cloud
```

### Misión 1 — Línea Base en Java 8 y Diagnóstico (`15 min`)
**En terminal de Cloud Shell:**
```bash
source ./scripts/use-java.sh 8
mvn clean test
mvn spring-boot:run
# Abrir Web Preview -> Preview on port 8080 (Banner Ámbar + Buró ~1,350 ms)
# Luego detener con Ctrl + C
```
**Prompt para Antigravity:**
```text
Usa la skill java8-legacy-assessment para auditar la deuda técnica de este proyecto y generarme el plan completo de modernización hacia Java 21 y Cloud Run.
```

---

### Misión 2 — Salto de Plataforma: Java 21, Spring Boot 3.3, `jakarta.*` y JUnit 5 (`25 min`)
**En terminal de Cloud Shell:**
```bash
source ./scripts/use-java.sh 21
```
**Prompt para Antigravity:**
```text
Usa la skill spring-boot3-jakarta-upgrade para actualizar el proyecto a Java 21, Spring Boot 3.3.5, Jakarta EE 10 y JUnit 5, y valida que 'mvn clean test' pase en verde.
```
**Validación en Web Preview (`8080`):**
```bash
mvn spring-boot:run
# El banner superior cambia automáticamente a Verde Esmeralda (MODERNIZADO: JAVA 21 LTS ACTIVO)
```

---

### Misión 3 — Refactorización Idiomática a Java 21 (`25 min`)
**Prompt para Antigravity:**
```text
Usa la skill java21-idiomatic-refactoring para refactorizar los modelos, DTOs, el motor de cálculo de riesgo y el reporte regulatorio a código idiomático de Java 21, y verifica con 'mvn test'.
```
**Validación en Web Preview (`8080`):**
```bash
mvn spring-boot:run
# Originar un nuevo crédito en el formulario derecho, ver 'Último' actualizado con getLast() y abrir el modal JSON de Dictamen.
```

---

### Misión 4 — Concurrencia con Virtual Threads / Project Loom (`15 min`)
**Prompt para Antigravity:**
```text
Usa la skill java21-virtual-threads para modernizar CreditBureauService con Virtual Threads de Java 21 y valida con 'mvn test'.
```
**Validación en Web Preview (`8080`):**
```bash
mvn spring-boot:run
# Hacer clic en 'Consultar Buró Masivo': el tiempo cae de ~1,350 ms a ~230 ms (~6x más rápido) usando VirtualThread[#...].
```

---

### Misión 5 — De Google App Engine (`app.yaml`) a Google Cloud Run (`20 min`)
**Prompt para Antigravity:**
```text
Usa la skill appengine-to-cloudrun-migration para migrar nuestra aplicación desde el app.yaml legado de App Engine, generar el Dockerfile multi-stage para Java 21 y desplegar el servicio en Google Cloud Run.
```
**Validación en Producción (URL `.run.app`):**
- Abrir la URL devuelta por `gcloud run deploy` (`https://fincore-risk-engine-...run.app`).
- Verificar el badge superior verde **`CLOUD RUN ACTIVO: fincore-risk-engine`**, el banner **`JAVA 21 LTS ACTIVO`** y el benchmark del Buró corriendo en **~230 ms** sobre Google Cloud Run.
