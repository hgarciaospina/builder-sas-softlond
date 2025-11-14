<p align="center">
  <img src="builders-sas-enterprise.png" width="100%" />
</p>

# Builders‑SAS – Documentación Enterprise de Arquitectura, API y Concurrencia

Sistema: **Builders‑SAS – Motor de Solicitudes, Órdenes y Notificaciones para Construcción**

---

## 1. Visión General del Sistema

Builders‑SAS es una plataforma orientada a la gestión de **solicitudes de construcción** (`ConstructionRequest`) que se transforman en **órdenes de construcción** (`ConstructionOrder`) de forma **100% automática**, impulsada por:

- **CRON Jobs inteligentes** (mañana y noche).
- **Notificaciones asincrónicas** hacia un *webhook externo*.
- **Reintentos automáticos** ante fallos de comunicación.
- **Procesamiento concurrente** con `CompletableFuture`.
- **Separación clara de responsabilidades** (domain, services, cron, notifications).

🔑 **Punto clave:**  
No existe ningún endpoint público para crear órdenes manualmente.  
Las `ConstructionOrder` se generan *exclusivamente* a partir de `ConstructionRequest` aprobadas.

---

## 2. Arquitectura General (Backend)

### 2.1 Diagrama de Alto Nivel

```text
Usuario / Frontend
  │
  ├── POST /api/v1/construction-requests
  ▼
ConstructionRequestService
  │   ├─ Validación (usuario, proyecto, tipo)
  │   ├─ Persistencia (JPA / R2DBC)
  │   └─ Notificación de creación
  ▼
MorningCronService (07:00)
  │   ├─ Lee requests APPROVED
  │   ├─ Calcula fechas inicio/fin
  │   ├─ Genera ConstructionOrder
  │   └─ Notifica inicio de orden
  ▼
NightCronService (22:00)
  │   ├─ Localiza órdenes que terminan HOY
  │   ├─ Cambia estado a FINISHED
  │   └─ Notifica finalización de orden
  ▼
NotificationService (async + retries)
  │   ├─ Envío a Webhook externo
  │   ├─ Reintentos con backoff simple
  │   └─ Almacenamiento en memoria (NotificationStorage)
  ▼
Frontend (Angular)
  │   ├─ Campana con badge (no leídas)
  │   ├─ Modal de notificaciones
  │   └─ Panel profesional de eventos
```

---

## 3. Módulos y Responsabilidades

### 3.1 ConstructionRequestService

Responsable de todo el ciclo de vida de las **solicitudes**:

- Crear solicitudes nuevas (estado inicial: `PENDING`).
- Validar usuario, proyecto y tipo de construcción.
- Transicionar a `APPROVED` / `REJECTED` (según reglas de negocio).
- Disparar notificación interna `construction_request_created`.

**Flujo de la funcionalidad:**

1. El cliente invoca `POST /api/v1/construction-requests?userId=...`.
2. El servicio valida:
   - Usuario solicitante.
   - Proyecto asociado.
   - Tipo de construcción.
3. Persiste el registro como `PENDING`.
4. Construye un `NotificationDto` con:
   - `eventType = "construction_request_created"`.
   - `timestamp` del sistema.
   - `payload` con datos relevantes (proyecto, tipo, coordenadas, etc.).
5. Envía la notificación de forma **asincrónica** a `NotificationService`.

---

### 3.2 ConstructionOrderService (NO expuesto como API pública)

No existe controlador que permita crear órdenes manualmente.  
Este servicio es utilizado **únicamente** por los CRON jobs:

- Crear órdenes (`ConstructionOrder`) desde `ConstructionRequest APPROVED`.
- Calcular fechas de inicio/fin con lógica de negocio correcta.
- Actualizar estados (`SCHEDULED`, `IN_PROGRESS`, `FINISHED`).
- Disparar notificaciones de `order_started` y `order_finished`.

📌 **Regla de cálculo de fechas (corregida):**

> La construcción **siempre inicia el día siguiente**:
> - Al día de la solicitud, si es el primer trabajo del proyecto.
> - Al día de finalización de la construcción anterior, si ya existían trabajos en el proyecto.

Ejemplo:

- Solicitud: 01/01/2025
- Duración: 3 días calendario
- Inicio: 02/01/2025
- Días: 02, 03, 04
- Fin: 04/01/2025 (❌ no se resta 1)

---

### 3.3 MorningCronService (CRON de la mañana)

**Función:** orquestar el paso de `ConstructionRequest APPROVED` a `ConstructionOrder` en curso.

**Horario recomendado (application.yml):**

```yaml
builders:
  cron:
    morning: "0 0 7 * * *"   # 07:00 AM todos los días
```

**Flujo de la funcionalidad:**

1. Se ejecuta el CRON configurado.
2. Consulta todas las `ConstructionRequest` con:
   - `status = APPROVED`
   - `order` aún no creada.
3. Para cada request:
   - Obtiene la última orden del proyecto (si existe).
   - Calcula fecha de inicio:
     - Si no hay órdenes previas → día siguiente de la solicitud.
     - Si las hay → día siguiente del fin de la última orden.
   - Calcula fecha de fin: `startDate + (durationDays - 1)` respetando la regla de negocio.
   - Crea `ConstructionOrder`.
   - Marca la request como “planificada / enlazada”.
4. Envía notificación:
   - `eventType = "construction_order_started"`.

---

### 3.4 NightCronService (CRON de la noche)

**Función:** cerrar automáticamente órdenes que finalizan el día actual.

**Horario recomendado:**

```yaml
builders:
  cron:
    night: "0 0 22 * * *"   # 10:00 PM todos los días
```

**Flujo:**

1. Se ejecuta el CRON nocturno.
2. Consulta todas las órdenes con:
   - `status = IN_PROGRESS`
   - `endDate = LocalDate.now()`.
3. Para cada orden:
   - Actualiza a `FINISHED`.
   - Dispara notificación `construction_order_finished`.

---

## 4. Sistema de Notificaciones Asíncronas y Reintentos

### 4.1 Componentes clave

- `NotificationDto` – DTO inmutable para transportar eventos.
- `NotificationService` – interfaz de alto nivel.
- `NotificationServiceImpl` – implementación concreta:
  - Enriquecimiento del DTO (timestamp, trackingId, etc.).
  - Envío asíncrono a webhook externo.
  - Reintentos ante errores de red o HTTP.
  - Persistencia temporal en memoria.
- `NotificationStorage` – colección en memoria para consulta del frontend.

### 4.2 Flujo de notificaciones

```text
Evento en negocio (request u orden)
  │
  ├─ Construcción de NotificationDto (eventType, timestamp, payload)
  ▼
NotificationService.sendAsync(dto)
  │
  ├─ CompletableFuture.supplyAsync(…)
  │     ├─ Realiza HTTP POST al webhook
  │     ├─ Si OK → registra éxito
  │     └─ Si Error → dispara lógica de retry
  │
  └─ CompletableFuture.thenAccept(…) para logging / métricas
  ▼
NotificationStorage.add(dto)
  │
  ▼
Frontend: GET /api/v1/notifications
```

---

### 4.3 Reintentos

Los reintentos están pensados para hacer el sistema más **robusto** sin bloquear el flujo principal.

Configuración (ejemplo):

```yaml
builders:
  notifications:
    webhookUrl: "https://webhook.site/tu-endpoint"
    maxRetries: 3
    retryDelayMillis: 2000
```

**Lógica típica de reintentos:**

1. Intento 1: envío al webhook.
2. Si falla (timeout / 5xx / excepción):
   - Espera `retryDelayMillis`.
   - Reintento 2.
3. Si vuelve a fallar:
   - Reintento 3.
4. Si todos fallan:
   - Se registra como error definitivo.
   - La notificación sigue disponible en el storage para auditorías.

Todo esto ocurre con `CompletableFuture`, por lo que los hilos de peticiones HTTP del usuario **nunca se bloquean** esperando a que el webhook responda.

---

## 5. ¿Por qué el sistema es 100% asíncrono y concurrente?

### 5.1 Uso de CompletableFuture

- Los métodos de notificación devuelven `CompletableFuture<Void>` o similar.
- `supplyAsync` o `runAsync` delegan el trabajo a un *thread pool* dedicado.
- El controlador HTTP puede responder inmediatamente al cliente.
- Los CRON jobs lanzan tareas que pueden ejecutarse en paralelo (por proyecto, por orden, etc.).

### 5.2 Ventajas

- No se bloquean hilos del servidor mientras se espera respuesta de un webhook, BD u otro sistema externo.
- Se soportan muchos más usuarios concurrentes con menos recursos.
- La latencia percibida por el cliente es menor.
- Permite escalar horizontalmente el backend sin cambios en la API.

---

## 6. Documentación de la API con Swagger / OpenAPI

### 6.1 Concepto

La API de Builders‑SAS está diseñada para ser **auto-documentada** usando **Swagger / OpenAPI**.  
Esto permite:

- Explorar endpoints desde un navegador.
- Probar llamadas (POST, GET, etc.) sin usar Postman.
- Ver modelos (`ConstructionRequest`, `NotificationDto`, etc.) en formato JSON.
- Generar clientes automáticamente (Java, TypeScript, etc.).

### 6.2 Endpoints típicos de Swagger

Una vez levantado el backend (por ejemplo en `http://localhost:9090`), suelen estar disponibles:

- UI interactiva:  
  `http://localhost:9090/swagger-ui.html`  
  o  
  `http://localhost:9090/swagger-ui/index.html`

- Documento OpenAPI en JSON:  
  `http://localhost:9090/v3/api-docs`

> ⚠️ La ruta exacta depende de la librería de Swagger / Springdoc utilizada en tu proyecto.  
> Si estás usando `springdoc-openapi-starter-webmvc-ui`, estas URIs son las estándar.

### 6.3 Principales recursos documentados en Swagger

Ejemplo de grupos lógicos de endpoints:

- **Construction Requests**
  - `POST /api/v1/construction-requests`
  - `GET /api/v1/construction-requests`
  - `GET /api/v1/construction-requests/{id}`
  - `PUT /api/v1/construction-requests/{id}/status`

- **Notifications**
  - `GET /api/v1/notifications` – listado de notificaciones en memoria.
  - `DELETE /api/v1/notifications` – limpieza de storage (si se implementa).

- **CRON Testing / Utilities**
  - `GET /api/v1/cron/test/progress?date=YYYY-MM-DD` – simulación de avance de órdenes.
  - Otros endpoints de prueba para orquestadores.

> ❌ No se incluyen endpoints como `POST /construction-orders` porque las órdenes se generan únicamente por lógica de negocio interna (CRON + servicios).

---

## 7. Versión HTML navegable de la documentación

En esta carpeta se incluye también una versión **HTML navegable**:

- `architecture.html`

Este archivo contiene:

- Índice con anclas para:
  - Visión general
  - Arquitectura
  - Módulos
  - Flujos funcionales
  - Notificaciones y reintentos
  - Asincronía y concurrencia
  - Swagger / OpenAPI
- Estructura semántica (`<section>`, `<h1>..h3>`, `<nav>`, etc.)
- Puede abrirse directamente en el navegador.

---

## 8. Diagramas UML

En la carpeta `docs/` se incluyen archivos **PlantUML** listos para abrir en:

- IntelliJ IDEA (plugin PlantUML).
- VS Code (PlantUML extension).
- Cualquier visualizador online de PlantUML.

Archivos:

- `uml-domain.puml` – Diagrama de clases de dominio y servicios principales.
- `uml-notifications-sequence.puml` – Diagrama de secuencia del flujo de notificaciones asincrónicas.

---

## 9. Estructura sugerida del repositorio

```text
builders-sas/
 ├── backend/
 │    ├── src/main/java/com/builderssas/api/...
 │    ├── src/main/resources/application.yml
 │    ├── Dockerfile
 │    └── ...
 ├── frontend/
 │    ├── src/...
 │    └── angular.json
 └── docs/
      ├── README.md                 # Este archivo
      ├── architecture.html         # Versión navegable en HTML
      ├── uml-domain.puml          # UML de dominio
      └── uml-notifications-sequence.puml   # UML de secuencia
```

---

## 10. Cómo usar estos archivos en tu repositorio

1. Copia todo el contenido de la carpeta `docs/` de este paquete ZIP dentro de la carpeta `docs/` de tu proyecto.
2. Verifica que Git detecta los archivos:

   ```bash
   git status
   ```

3. Confirma los cambios:

   ```bash
   git add docs/
   git commit -m "Add enterprise documentation, HTML and UML diagrams for Builders-SAS"
   git push origin main
   ```

4. Usa los archivos así:
   - `README.md` → para lectura en GitHub / GitLab, documentación oficial.
   - `architecture.html` → para navegar en el navegador o publicar en GitHub Pages / Intranet.
   - `uml-*.puml` → para generar imágenes UML (PNG/SVG) y usarlas en presentaciones, clases y PDFs.

---

## 11. Conclusión

Este paquete de documentación convierte a Builders‑SAS en un proyecto con:

- Arquitectura claramente definida.
- Flujos funcionales documentados.
- Concurrencia y asincronía explicadas.
- Documentación de API con Swagger/OpenAPI.
- Diagramas UML para técnica y negocio.
- Material adecuado para:
  - Formación de nuevos desarrolladores.
  - Auditorías técnicas.
  - Presentaciones académicas y profesionales.


---

## 👨‍💼 Autores Oficiales del Proyecto

### 🧑‍💻 Ingeniero de Sistemas  
**Henry García Ospina**  
📧 Correo: **henrygarciaospina@gmail.com**  
📱 Celular: **320 515 1194**

### 🧑‍🔧 Ingeniero Electrónico  
**Luis Ramos**  
📱 Celular: **305 282 6587**

---


## 12. Swagger UI – Documentación Ejecutable de la API (Modo “Envidia del Jefe”)

Esta sección explica, paso a paso, cómo usar el paquete de documentación para visualizar la API en **Swagger UI**, sin depender de que el backend esté corriendo.

### 12.1. Archivos involucrados

En la carpeta `docs/swagger-ui/` encontrarás:

- `index.html` → Lanzador de Swagger UI.
- `openapi.json` → Especificación OpenAPI 3 de la API Builders‑SAS.

> Swagger UI (CSS/JS) se carga desde CDN oficiales de Swagger.  
> Esto hace que el archivo sea liviano y fácil de mantener, pero **requiere conexión a Internet** para obtener los assets visuales.

### 12.2. Cómo abrir Swagger UI paso a paso

1. Asegúrate de tener esta estructura de carpetas:

   ```text
   builders-sas/
     └── docs/
          ├── README.md
          ├── architecture.html
          └── swagger-ui/
               ├── index.html
               └── openapi.json
   ```

2. Haz doble clic en:

   ```text
   docs/swagger-ui/index.html
   ```

3. Tu navegador abrirá una página con el logo de Swagger y un panel completo de documentación:

   - Lista de endpoints a la izquierda, agrupados por **tags**:
     - `Construction Requests`
     - `Notifications`
     - `Cron`
   - En el centro, cada endpoint con:
     - Método HTTP (GET/POST/PUT…)
     - URL
     - Parámetros
     - Request body (si aplica)
     - Respuestas con códigos (200, 201, 400, 404, 500…)
     - Ejemplos JSON

4. Haz clic en cualquier endpoint para expandirlo.

5. Pulsa el botón **“Try it out”** para ver cómo sería una llamada real (aunque aquí no ejecuta nada si el backend no está levantado; sirve como documentación viva).

---

### 12.3. Cómo funciona internamente este Swagger UI

Dentro de `index.html` hay un bloque como este (simplificado):

```html
<script src="https://unpkg.com/swagger-ui-dist/swagger-ui-bundle.js"></script>
<script>
  window.onload = () => {
    window.ui = SwaggerUIBundle({
      url: "openapi.json",
      dom_id: "#swagger-ui",
      presets: [SwaggerUIBundle.presets.apis],
      layout: "BaseLayout"
    });
  };
</script>
```

La magia ocurre así:

1. El navegador descarga los JS/CSS de Swagger desde el CDN `unpkg.com`.
2. Cuando la página carga (`window.onload`), Swagger UI lee el archivo local `openapi.json`.
3. Swagger transforma ese JSON en:
   - lista de endpoints,
   - modelos (schemas),
   - parámetros,
   - ejemplos.
4. Todo se dibuja dinámicamente en el `<div id="swagger-ui"></div>`.

---

### 12.4. ¿Cómo se genera `openapi.json`?

El archivo `openapi.json` resume toda la API de Builders‑SAS:

- Título, versión, descripción.
- Servidores (`http://localhost:9090`).
- Tags por módulo.
- Paths (endpoints).
- Schemas de los DTO:
  - `ConstructionRequest`
  - `ConstructionRequestCreate`
  - `NotificationDto`
  - `CronProgressResponse`
  - `ErrorResponse`

Este archivo se puede:

- Importar en **Swagger Editor**.
- Importar en **Postman**.
- Usar para generar clientes en:
  - Angular
  - Java
  - Python
  - C#
  - etc.

---

## 13. Diagramas y Recursos Visuales

Además de los diagramas en texto (ASCII) incluidos a lo largo del documento, el sistema está preparado para usar diagramas UML externos.

### 13.1. Archivos UML típicos

Se recomienda mantener en `docs/` archivos como:

- `uml-domain.puml` – Diagrama de clases de dominio (Project, ConstructionRequest, ConstructionOrder, NotificationDto, servicios).
- `uml-notifications-sequence.puml` – Diagrama de secuencia de notificaciones asíncronas (User → API → NotificationService → Webhook).

Estos archivos pueden visualizarse con:

- Plugin PlantUML en IntelliJ IDEA.
- Plugin PlantUML en VS Code.
- Cualquier visor online de PlantUML.

---

### 13.2. Cómo generar imágenes a partir de los .puml

1. Abre el archivo `.puml` en tu IDE con plugin PlantUML.
2. Selecciona **“Render”** o **“Show Diagram”**.
3. Exporta como PNG o SVG.
4. Guarda las imágenes en:

   ```text
   docs/img/
     ├── uml-domain.png
     └── uml-notifications-sequence.png
   ```

5. Desde `README.md` o `architecture.html`, puedes referenciarlas:

   ```md
   ![Diagrama de dominio](./img/uml-domain.png)
   ![Diagrama de secuencia de notificaciones](./img/uml-notifications-sequence.png)
   ```

---

## 14. Guía Rápida para Impresionar a tu Jefe 😏

1. Abre `architecture.html` en el navegador → muestra la visión general, arquitectura y módulos con diseño oscuro corporativo.
2. Abre `docs/swagger-ui/index.html` → recorre los endpoints y explica que la API está completamente descrita vía OpenAPI.
3. Enseña los `.puml` o imágenes UML exportadas → evidencia de diseño formal.
4. Abre `README.md` en GitHub → se ve toda la documentación técnica, flujos, asincronía, crons, notificaciones y reintentos explicados a nivel de arquitectura enterprise.

Con este paquete, la documentación de Builders‑SAS no solo es funcional, sino que se ve como el entregable de una consultora internacional de primer nivel.
