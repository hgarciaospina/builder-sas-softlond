# builders-sas-api — Modelo final y cómo arrancar (Documentación en español)

## Tabla de entidades (Inglés -> Español)

| English name | Nombre en español |
|---|---|
| User | Usuario |
| Engineer | Ingeniero |
| ConstructionType | TipoDeConstrucción |
| MaterialType | TipoMaterial |
| ConstructionTypeMaterial | Construcción_Tipo_Material |
| Project | Proyecto |
| ProjectConstruction | ConstrucciónEnProyecto |
| ConstructionOrder | OrdenDeConstrucción |
| ConstructionOrderSnapshot | SnapshotOrdenConstrucción |
| MaterialsStock | InventarioMateriales |
| ProjectMaterialReservation | ReservaMaterialProyecto |
| AuditLog | RegistroAuditoría |
| ProjectCancellation | CancelaciónProyecto |

## Resumen del modelo
El modelo usa tablas normalizadas. `ConstructionType` no almacena columnas por material;
ese dato está en `construction_type_materials` para permitir extensibilidad.
Las entidades usan enums en código para lógica y se persisten como cadenas (STRING) en la base.

## Estados y nombres de columna
- Project: `projectStatus` -> columna `project_status` (values: PLANNED, IN_PROGRESS, COMPLETED, CANCELLED)
- ProjectConstruction: `constructionStatus` -> columna `construction_status` (values: PENDING, IN_PROGRESS, COMPLETED)
- ConstructionOrder: `orderStatus` -> columna `order_status` (values: REQUESTED, SCHEDULED, IN_PROGRESS, FINISHED, CANCELLED)

> Nota: se usan enums en el código para consistencia. La base almacena strings para permitir consultas y migraciones futuras.

## Instrucciones para ejecutar (desarrollo)
1. Clone el repositorio.
2. Ajuste `src/main/resources/application.properties` con la URL/usuario/password de Postgres.
3. Ejecute `mvn spring-boot:run`.
4. En el primer arranque el `CommandLineRunner` `DataInitializer` poblara `material_types`, `construction_types` y la tabla `construction_type_materials` si están vacías. Si ya existen, no volverá a insertar datos (verificación por código).

## Notas de producción
- **No** use `spring.jpa.hibernate.ddl-auto=update` en producción. Use Flyway o Liquibase y controle las migraciones.
- Revise y ajuste los timeouts de transacción y lock para procesos de reserva de inventario.

## Archivos importantes generados
- `src/main/java/com/builderssas/api/domain/model` — entidades Java con JavaDoc en inglés.
- `src/main/java/com/builderssas/api/domain/model/enums` — enums usados en servicios.
- `src/main/java/com/builderssas/api/config/DataInitializer.java` — seed runner seguro.
- `src/main/resources/db` — scripts DDL y seed (si prefiere ejecutar manualmente).

Fin del README.
