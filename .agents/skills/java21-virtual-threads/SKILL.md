---
name: java21-virtual-threads
description: >-
  Modernizes the blocking I/O concurrency engine in CreditBureauService from a legacy fixed OS platform thread pool to Java 21 Virtual Threads (Project Loom) using Executors.newVirtualThreadPerTaskExecutor() and direct Thread.isVirtual() calls. Use this skill when the user asks to modernize concurrency, speed up the Credit Bureau batch check, or enable Virtual Threads / Project Loom (Mission 4).
---

# Skill: Concurrencia Moderna con Virtual Threads / Project Loom (Misión 4)

Usa esta skill cuando el usuario pida resolver el cuello de botella del Buró de Crédito en `CreditBureauService.java` utilizando **Virtual Threads de Java 21 (Project Loom)**.

## Contexto del Problema

En el código legado (Java 8):
- `CreditBureauService` utiliza `Executors.newFixedThreadPool(2)`.
- Cada consulta al Buró Nacional de Crédito tiene una latencia bloqueante simulada de `220 ms` (`Thread.sleep(220)`).
- Evaluar las 12 solicitudes del portafolio requiere 6 tandas secuenciales de 2 hilos $\rightarrow$ **`~1,350 ms`**.

Con **Java 21 Virtual Threads (`Executors.newVirtualThreadPerTaskExecutor()`)**:
- Las 12 tareas bloqueantes se montan en hilos virtuales ultraligeros que liberan el *carrier thread* durante el `Thread.sleep(220)`.
- Las 12 solicitudes se completan en paralelo en **`~230 ms` (~6x más rápido)**.

## Pasos de Ejecución en `CreditBureauService.java`

1. **Reemplazar el `ExecutorService`:**
   - Cambia `private final ExecutorService bureauExecutor = Executors.newFixedThreadPool(2);` por:
     ```java
     private final ExecutorService bureauExecutor = Executors.newVirtualThreadPerTaskExecutor();
     ```
2. **Simplificar con Lambdas y `Stream.toList()`:**
   - Reemplaza la creación manual de clases anónimas `new Callable<CreditApplication>() { ... }` por lambdas concisas:
     ```java
     List<Callable<CreditApplication>> tasks = applications.stream()
             .<Callable<CreditApplication>>map(app -> () -> verifySingleApplicationWithBlockingIo(app))
             .toList();
     ```
   - Reemplaza `.collect(Collectors.toList())` por `.toList()`.
3. **Usar API nativa de Java 21 `Thread.isVirtual()` y `threadId()`:**
   - En `verifySingleApplicationWithBlockingIo()` y `checkIfThreadIsVirtual()`, elimina el uso de reflexión (`Method isVirtualMethod = Thread.class.getMethod("isVirtual")`) y llama directamente a **`current.isVirtual()`** y **`current.threadId()`**:
     ```java
     boolean isVirtual = current.isVirtual();
     String threadDescriptor = isVirtual
             ? "VirtualThread[" + (current.getName().isEmpty() ? "loom-vt" : current.getName()) + "#" + current.threadId() + "]"
             : current.getName() + " (OS Platform Thread)";
     ```
   - En `isExecutorUsingVirtualThreads()`, simplifica con lambda:
     ```java
     Future<Boolean> probe = bureauExecutor.submit(() -> Thread.currentThread().isVirtual());
     ```
4. **Reemplazar `SimpleDateFormat` por `DateTimeFormatter`:**
   - Usa `LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))`.

## Verificación
Ejecuta `source ./scripts/use-java.sh 21 && mvn test` para comprobar que el proyecto compila y pasa todas las pruebas unitarias. Indica al usuario que levante `mvn spring-boot:run` y presione el botón **"Consultar Buró Masivo (12 Créditos)"** en la interfaz web para ver caer el tiempo de **~1,350 ms a ~230 ms**.
