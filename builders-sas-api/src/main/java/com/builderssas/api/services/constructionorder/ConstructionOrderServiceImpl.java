package com.builderssas.api.services.constructionorder;

import com.builderssas.api.domain.model.construction.ConstructionOrder;
import com.builderssas.api.domain.model.construction.ConstructionRequest;
import com.builderssas.api.domain.model.construction.ConstructionType;
import com.builderssas.api.domain.model.construction.ConstructionTypeMaterial;
import com.builderssas.api.domain.model.constructionorder.dto.MaterialConsumptionDto;
import com.builderssas.api.domain.model.enums.OrderStatus;
import com.builderssas.api.domain.model.enums.RequestStatus;
import com.builderssas.api.domain.model.notification.NotificationDto;
import com.builderssas.api.repository.ConstructionOrderRepository;
import com.builderssas.api.repository.ConstructionTypeRepository;
import com.builderssas.api.repository.MaterialTypeRepository;
import com.builderssas.api.services.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConstructionOrderServiceImpl implements ConstructionOrderService {

    private final ConstructionOrderRepository orderRepo;
    private final MaterialTypeRepository materialRepo;
    private final ConstructionTypeRepository typeRepo;
    private final NotificationService notificationService;

    private static final String ORDER_CREATED_TEMPLATE = """
        ✔ ORDEN CREADA
        Solicitud: %s
        Número de Orden: %s
        Tipo de Construcción: %s
        Fecha de Inicio: %s
        Fecha de Terminación: %s
        Coordenadas: (%s, %s)
        """;


    // ============================================================================================
    // ✅ CREAR ORDEN DESDE SOLICITUD — FUNCIONAL + ASÍNCRONO
    // ============================================================================================
    @Override
    public CompletableFuture<ConstructionOrder> createOrderFromRequest(final ConstructionRequest req) {

        final var type = Optional.ofNullable(req)
                .map(ConstructionRequest::getConstructionType)
                .map(ConstructionType::getId)
                .flatMap(typeRepo::findByIdFetchMaterials)
                .orElseThrow(() -> new IllegalStateException("Solicitud o ConstructionType inválidos (no se encontró el tipo en BD)"));

        final var durationDays = Optional.ofNullable(type.getDurationDays())
                .filter(d -> d > 0)
                .orElseThrow(() -> new IllegalStateException(
                        "durationDays inválido o null para el tipo '" + Optional.ofNullable(type.getName()).orElse("unknown") + "'"));

        log.info("🧮 Preparando cálculo para requestId={}, projectId={}, type='{}', durationDays={}, requestDate={}",
                Optional.ofNullable(req).map(ConstructionRequest::getId).orElse(null),
                Optional.ofNullable(req).map(ConstructionRequest::getProject).map(p -> p.getId()).orElse(null),
                Optional.ofNullable(type.getName()).orElse("unknown"),
                durationDays,
                Optional.ofNullable(req).map(ConstructionRequest::getRequestDate).orElse(null));

        return calculateStartDateAsync(req)
                .thenCompose(start ->
                        validateRequest(req)
                                .<CompletableFuture<ConstructionOrder>>map(error -> failRequest(req, error))
                                .orElseGet(() -> {
                                    final var deliveryDays = 1;
                                    final var end = start.plusDays((long) durationDays + deliveryDays);

                                    log.info("📅 Fechas: start={}, end={}, requestDate={}, durationDays={}, deliveryDays={}",
                                            start,
                                            end,
                                            Optional.ofNullable(req).map(ConstructionRequest::getRequestDate).orElse(null),
                                            durationDays,
                                            deliveryDays
                                    );

                                    final var rows = buildConsumptionRows(type);
                                    final var observations = buildObservations(rows);
                                    final var order = buildOrder(req, start, end, observations);

                                    return applyStockDiscountAsync(rows, type.getMaterials().stream().toList())
                                            .thenCompose(v -> saveOrderAsync(order))
                                            .thenApply(saved -> updateProjectEnd(req, saved))
                                            .whenComplete((o, ex) -> sendOrderNotification(req, o, ex));
                                })
                );
    }

    // ============================================================================================
    // ✅ VALIDACIÓN — Devuelve Optional<String> con el detalle si hay problemas
    // ============================================================================================
    /* MODIFICACIÓN: corregida para que una solicitud válida devuelva Optional.empty() */
    private Optional<String> validateRequest(final ConstructionRequest req) {
        // Si la solicitud viene realmente null, esa sí es una inconsistencia grave.
        if (req == null) {
            return Optional.of("Solicitud null");
        }

        // Construimos el detalle de errores de campos.
        String detail = Stream.of(
                        Optional.ofNullable(req.getProject())
                                .map(x -> "")
                                .orElse("Proyecto null"),
                        Optional.ofNullable(req.getConstructionType())
                                .map(x -> "")
                                .orElse("ConstructionType null en request"),
                        Optional.ofNullable(req.getRequestedBy())
                                .map(x -> "")
                                .orElse("RequestedBy null"),
                        Optional.ofNullable(req.getRequestDate())
                                .map(x -> "")
                                .orElse("requestDate null"),
                        Optional.ofNullable(req.getLatitude())
                                .map(x -> "")
                                .orElse("Latitude null"),
                        Optional.ofNullable(req.getLongitude())
                                .map(x -> "")
                                .orElse("Longitude null")
                )
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("; "));

        // Si no hay errores, la validación es correcta → Optional.empty()
        if (detail.isEmpty()) {
            return Optional.empty();
        }

        // Si hay al menos un problema, se devuelve el detalle.
        return Optional.of(detail);
    }

    private CompletableFuture<ConstructionOrder> failRequest(final ConstructionRequest req, final String detail) {
        Optional.ofNullable(req)
                .map(r -> {
                    r.setRequestStatus(RequestStatus.FAILED);
                    r.setObservations(
                            Optional.ofNullable(r.getObservations())
                                    .filter(s -> !s.isBlank())
                                    .map(s -> s + " ")
                                    .orElse("") + "[FAILED] " + detail
                    );
                    return r;
                })
                .ifPresent(ignored -> {
                    // Lógica original: se mantiene la notificación global
                    NotificationDto dto = NotificationDto.builder()
                            .eventType("REQUEST_FAILED")
                            .payload("Solicitud " +
                                    Optional.ofNullable(req).map(ConstructionRequest::getId).orElse(null)
                                    + " rechazada por datos inconsistentes: " + detail)
                            .build();

                    notificationService.send(dto); // se conserva

                    /* MODIFICACIÓN: envío adicional asociado al usuario dueño */
                    Long targetUserId = Optional.ofNullable(req)
                            .map(ConstructionRequest::getRequestedBy)
                            .map(u -> u.getId())
                            .orElse(null);

                    notificationService.sendForUser(dto, targetUserId);
                });

        final var failed = new CompletableFuture<ConstructionOrder>();
        failed.completeExceptionally(new IllegalStateException("Datos inconsistentes para crear la orden: " + detail));
        return failed;
    }

    // ============================================================================================
    // ✅ FECHAS — Cálculo funcional de start (regla: +1 día sobre request o sobre último fin)
    // ============================================================================================
    private CompletableFuture<LocalDate> calculateStartDateAsync(final ConstructionRequest req) {
        return CompletableFuture.supplyAsync(() ->
                Optional.ofNullable(req)
                        .flatMap(r ->
                                Optional.ofNullable(r.getProject()).map(p -> p.getId())
                                        .flatMap(orderRepo::findLastEndDate)
                                        .map(lastEnd -> lastEnd.plusDays(1))
                                        .or(() -> Optional.ofNullable(r.getRequestDate()).map(d -> d.plusDays(1)))
                        )
                        .orElseThrow(() -> new IllegalStateException("No se pudo calcular startDate: solicitud o requestDate null"))
        ).whenComplete((start, ex) ->
                log.info("▶️ StartDateCalc -> projectId={}, requestDate={}, start={}, error={}",
                        Optional.ofNullable(req).map(ConstructionRequest::getProject).map(p -> p.getId()).orElse(null),
                        Optional.ofNullable(req).map(ConstructionRequest::getRequestDate).orElse(null),
                        start,
                        Optional.ofNullable(ex).map(Throwable::getMessage).orElse(null)
                )
        );
    }

    // ============================================================================================
    // ✅ CONSUMO DE MATERIALES — Mapea relaciones → DTO de consumo
    // ============================================================================================
    private List<MaterialConsumptionDto> buildConsumptionRows(final ConstructionType type) {
        return Optional.ofNullable(type)
                .stream()
                .flatMap(t -> t.getMaterials().stream())
                .map(rel -> Optional.ofNullable(rel.getMaterialType())
                        .map(mt -> MaterialConsumptionDto.builder()
                                .materialName(mt.getName())
                                .stockBefore(mt.getStock())
                                .required(rel.getQuantityRequired())
                                .stockAfter(mt.getStock() - rel.getQuantityRequired())
                                .build()
                        ).orElseThrow(() -> new IllegalStateException("Relación sin MaterialType")))
                .toList();
    }

    private String buildObservations(final List<MaterialConsumptionDto> rows) {
        return new StringBuilder("Material | StockAntes | Requerido | StockDespués\n")
                .append(
                        rows.stream()
                                .map(r -> r.getMaterialName() + " | " + r.getStockBefore() + " | " + r.getRequired() + " | " + r.getStockAfter())
                                .collect(Collectors.joining("\n"))
                ).toString();
    }

    // ============================================================================================
    // ✅ CONSTRUIR ORDEN — construcción inmutable vía builder
    // ============================================================================================
    private ConstructionOrder buildOrder(
            final ConstructionRequest req,
            final LocalDate start,
            final LocalDate end,
            final String observations
    ) {
        return Optional.ofNullable(req)
                .map(r -> ConstructionOrder.builder()
                        .project(r.getProject())
                        .constructionType(r.getConstructionType())
                        .requestedBy(r.getRequestedBy())
                        .constructionRequest(r)
                        .latitude(r.getLatitude())
                        .longitude(r.getLongitude())
                        .requestedDate(r.getRequestDate())
                        .scheduledStartDate(start)
                        .scheduledEndDate(end)
                        .orderStatus(OrderStatus.PENDING)
                        .observations(observations)
                        .build())
                .orElseThrow(() -> new IllegalStateException("Solicitud null al construir la orden"));
    }

    // ============================================================================================
    // ✅ DESCONTAR STOCK — Sin forEach; transformar → saveAll
    // ============================================================================================
    private CompletableFuture<Void> applyStockDiscountAsync(
            final List<MaterialConsumptionDto> rows,
            final List<ConstructionTypeMaterial> materials
    ) {
        return CompletableFuture.runAsync(() ->
                materials.stream()
                        .map(ConstructionTypeMaterial::getMaterialType)
                        .map(mt ->
                                rows.stream()
                                        .filter(r -> Objects.equals(r.getMaterialName(), mt.getName()))
                                        .findFirst()
                                        .map(r -> {
                                            mt.setStock(r.getStockAfter()); // efecto controlado sobre entidad JPA
                                            return mt;
                                        })
                                        .orElseThrow(() -> new IllegalStateException("No se encontró consumo para material: " + mt.getName()))
                        )
                        .collect(Collectors.collectingAndThen(Collectors.toList(), materialRepo::saveAll))
        );
    }

    // ============================================================================================
    // ✅ GUARDAR ORDEN — asíncrono puro
    // ============================================================================================
    private CompletableFuture<ConstructionOrder> saveOrderAsync(final ConstructionOrder order) {
        return CompletableFuture.supplyAsync(() -> orderRepo.save(order));
    }

    // ============================================================================================
    // ✅ ACTUALIZAR PROYECTO — efecto mínimo y localizado
    // ============================================================================================
    private ConstructionOrder updateProjectEnd(final ConstructionRequest req, final ConstructionOrder saved) {
        Optional.ofNullable(req)
                .map(ConstructionRequest::getProject)
                .ifPresent(p -> p.setProjectEndDate(saved.getScheduledEndDate()));
        return saved;
    }

    // ============================================================================================
    // ✅ NOTIFICACIONES — ramificación funcional por presencia de error/éxito
    // ============================================================================================
    private void sendOrderNotification(final ConstructionRequest req, final ConstructionOrder o, final Throwable ex) {

        /* MODIFICACIÓN: userId del dueño de la solicitud */
        Long targetUserId = Optional.ofNullable(req)
                .map(ConstructionRequest::getRequestedBy)
                .map(u -> u.getId())
                .orElse(null);

        Optional.ofNullable(ex)
                .map(err -> {
                            // Lógica original: notificación global
                            NotificationDto dto = NotificationDto.builder()
                                    .eventType("ORDER_CREATION_FAILED")
                                    .payload("Error creando orden para solicitud " +
                                            Optional.ofNullable(req).map(ConstructionRequest::getId).orElse(null) + ": " +
                                            Optional.ofNullable(err.getCause()).map(Throwable::getMessage).orElse(err.getMessage()))
                                    .build();

                            notificationService.send(dto); // se conserva

                            /* MODIFICACIÓN: envío adicional al usuario dueño */
                            notificationService.sendForUser(dto, targetUserId);
                            return dto;
                        }
                )
                .or(() ->
                        Optional.ofNullable(o).map(ok -> {
                            NotificationDto dto = NotificationDto.builder()
                                    .eventType("ORDER_CREATED")
                                    .payload(
                                            ORDER_CREATED_TEMPLATE.formatted(
                                                    Optional.ofNullable(req).map(ConstructionRequest::getId).orElse(null),
                                                    ok.getId(),
                                                    ok.getConstructionType().getName(),
                                                    ok.getScheduledStartDate(),
                                                    ok.getScheduledEndDate(),
                                                    Optional.ofNullable(req).map(ConstructionRequest::getLatitude).orElse(null),
                                                    Optional.ofNullable(req).map(ConstructionRequest::getLongitude).orElse(null)
                                            )
                                    ).build();

                            notificationService.send(dto); // se conserva

                                    /* MODIFICACIÓN: envío adicional al usuario dueño */
                                    notificationService.sendForUser(dto, targetUserId);
                                    return dto;
                                }
                        )
                );
    }
}
