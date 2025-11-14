package com.builderssas.api.services.constructionrequest;

import com.builderssas.api.core.exception.GlobalExceptionHandler;
import com.builderssas.api.domain.model.construction.ConstructionRequest;
import com.builderssas.api.domain.model.constructionrequest.dto.ConstructionRequestDto;
import com.builderssas.api.domain.model.constructionrequest.dto.CreateConstructionRequestDto;
import com.builderssas.api.domain.model.enums.RequestStatus;
import com.builderssas.api.repository.*;
import com.builderssas.api.services.constructionorder.ConstructionOrderService;
import com.builderssas.api.services.notification.NotificationService;
import com.builderssas.api.domain.model.notification.NotificationDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConstructionRequestServiceImpl implements ConstructionRequestService {

    private final ConstructionRequestRepository requestRepo;
    private final ConstructionOrderRepository orderRepo;
    private final ProjectRepository projectRepo;
    private final ConstructionTypeRepository typeRepo;
    private final UserRepository userRepo;
    private final ConstructionOrderService orderService;
    private final NotificationService notificationService;

    // ================================================================================================
    // CREATE
    // ================================================================================================
    @Transactional
    @Override
    public ConstructionRequestDto create(CreateConstructionRequestDto dto, Long userId) {

        //  AQUI VA LA VALIDACIÓN — PROGRAMACIÓN FUNCIONAL
        // ========================================================================
//  VALIDACIÓN DE PERMISOS PARA CREAR SOLICITUDES
// ========================================================================

// 🔍 LOG DE DIAGNÓSTICO: muestra los roles reales cargados por JPA
        log.warn("DEBUG VALIDACIÓN — Roles reales del usuario {}: {}",
                userId,
                userRepo.findById(userId)
                        .map(u -> u.getRoles().stream()
                                .map(ur -> ur.getRole().getName())
                                .toList()
                        )
                        .orElse(List.of("Usuario no encontrado"))
        );

// 🔐 VALIDACIÓN REAL Y ROBUSTA
        Optional.of(userId)
                .filter(id ->
                        userRepo.findById(id)
                                .map(u ->
                                        u.getRoles().stream()
                                                .map(ur -> ur.getRole().getName().trim().toUpperCase())
                                                .anyMatch("ROLE_ARCHITECT"::equals)
                                )
                                .orElse(false)
                )
                .orElseThrow(() -> new GlobalExceptionHandler.UnauthorizedException(
                        "Se requiere ROLE_ARCHITECT para crear solicitudes."
                ));



        // NO SE TOCA NADA MÁS — esto queda EXACTAMENTE como estaba
        return loadProject(dto.getProjectId())
                .flatMap(project ->
                        loadTypeFetchMaterials(dto.getConstructionTypeId())
                                .flatMap(type ->
                                        loadUser(userId)
                                                .map(user -> buildEntity(dto, project, type, user))
                                )
                )
                .map(req -> stagePendingOrRejected(req, dto))
                .map(requestRepo::save)
                .map(this::sendNotificationOnCreation)
                .map(this::triggerOrderIfPending)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalStateException("Error creando solicitud"));
    }

    // ================================================================================================
    // NOTIFICACIÓN — CREACIÓN (GLOBAL + POR USUARIO)
    // ================================================================================================
    private ConstructionRequest sendNotificationOnCreation(ConstructionRequest req) {
        log.warn("✅ VAMPI LOG — Entró a sendNotificationOnCreation() para request {}", req.getId());

        var event = switch (req.getRequestStatus()) {
            case PENDING -> "CONSTRUCTION_REQUEST_CREATED";
            case REJECTED -> "CONSTRUCTION_REQUEST_REJECTED";
            default -> null;
        };

        Optional.ofNullable(event).ifPresent(evt -> {

            var dto = NotificationDto.builder()
                    .eventType(evt)
                    .payload(
                            "Solicitud %s creada con estado %s. Proyecto=%s, Tipo=%s, Coordenadas=(%s,%s). %s"
                                    .formatted(
                                            req.getId(),
                                            req.getRequestStatus(),
                                            req.getProject().getName(),
                                            req.getConstructionType().getName(),
                                            req.getLatitude(),
                                            req.getLongitude(),
                                            Optional.ofNullable(req.getObservations()).orElse("")
                                    )
                    )
                    .build();

            // Notificación GLOBAL (original, se conserva)
            notificationService.send(dto);

            // Notificación por usuario
            notificationService.sendForUser(dto, req.getRequestedBy().getId());
        });

        return req;
    }

    // ================================================================================================
    // NOTIFICACIÓN FINAL — APROBADA / FALLIDA (GLOBAL + POR USUARIO)
    // ================================================================================================
    private ConstructionRequest sendNotificationFinalStatus(ConstructionRequest req) {

        var event = switch (req.getRequestStatus()) {
            case APPROVED -> "CONSTRUCTION_REQUEST_APPROVED";
            case FAILED -> "CONSTRUCTION_REQUEST_FAILED";
            default -> null;
        };

        Optional.ofNullable(event).ifPresent(evt -> {

            var dto = NotificationDto.builder()
                    .eventType(evt)
                    .payload(
                            "Solicitud %s terminó en estado %s. %s"
                                    .formatted(
                                            req.getId(),
                                            req.getRequestStatus(),
                                            Optional.ofNullable(req.getObservations()).orElse("")
                                    )
                    )
                    .build();

            notificationService.send(dto);
            notificationService.sendForUser(dto, req.getRequestedBy().getId());
        });

        return req;
    }

    // ================================================================================================
    // LOADERS
    // ================================================================================================
    private Optional<com.builderssas.api.domain.model.project.Project> loadProject(Long id) {
        return projectRepo.findById(id);
    }

    private Optional<com.builderssas.api.domain.model.construction.ConstructionType> loadTypeFetchMaterials(Long id) {
        return typeRepo.findByIdFetchMaterials(id);
    }

    private Optional<com.builderssas.api.domain.model.user.User> loadUser(Long id) {
        return userRepo.findById(id);
    }

    // ================================================================================================
    // BUILDER
    // ================================================================================================
    private ConstructionRequest buildEntity(
            CreateConstructionRequestDto dto,
            com.builderssas.api.domain.model.project.Project project,
            com.builderssas.api.domain.model.construction.ConstructionType type,
            com.builderssas.api.domain.model.user.User user
    ) {
        return ConstructionRequest.builder()
                .project(project)
                .constructionType(type)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .requestedBy(user)
                .requestDate(LocalDate.now())
                .build();
    }

    // ================================================================================================
    // VALIDACIONES
    // ================================================================================================
    private ConstructionRequest stagePendingOrRejected(ConstructionRequest req, CreateConstructionRequestDto dto) {

        var coordLibre = isCoordinateAvailable(dto.getLatitude(), dto.getLongitude());
        var stockOk = hasSufficientStock(req);

        return Optional.of(req)
                .map(r ->
                        Optional.of(coordLibre && stockOk)
                                .filter(Boolean::booleanValue)
                                .map(__ ->
                                        r.withRequestStatus(RequestStatus.PENDING)
                                                .withObservations(
                                                        "Validaciones OK → coordenada (%s,%s) libre y stock suficiente."
                                                                .formatted(dto.getLatitude(), dto.getLongitude())
                                                )
                                )
                                .orElseGet(() ->
                                        r.withRequestStatus(RequestStatus.REJECTED)
                                                .withObservations(
                                                        Optional.of(coordLibre)
                                                                .filter(c -> !c)
                                                                .map(__ ->
                                                                        "Rechazada: Coordenada (%s,%s) ocupada por una ORDEN."
                                                                                .formatted(dto.getLatitude(), dto.getLongitude())
                                                                )
                                                                .orElse("Rechazada: Stock insuficiente para los materiales requeridos.")
                                                )
                                )
                )
                .orElse(req);
    }

    private boolean hasSufficientStock(ConstructionRequest req) {
        return req.getConstructionType()
                .getMaterials()
                .stream()
                .allMatch(rel -> rel.getMaterialType().getStock() >= rel.getQuantityRequired());
    }

    private boolean isCoordinateAvailable(Double lat, Double lng) {
        return !orderRepo.existsByLatitudeAndLongitude(lat, lng);
    }

    // ================================================================================================
    // CREACIÓN DE ORDEN ASÍNCRONA
    // ================================================================================================
    private ConstructionRequest triggerOrderIfPending(ConstructionRequest saved) {

        Optional.of(saved)
                .filter(r -> r.getRequestStatus() == RequestStatus.PENDING)
                .ifPresent(r ->
                        orderService.createOrderFromRequest(r)
                                .whenComplete((order, ex) -> {
                                    var updated = Optional.ofNullable(ex)
                                            .<ConstructionRequest>map(err ->
                                                    r.withRequestStatus(RequestStatus.FAILED)
                                                            .withObservations(
                                                                    (r.getObservations() == null ? "" : r.getObservations()) +
                                                                            "\nError creando orden: " +
                                                                            Optional.ofNullable(err.getCause())
                                                                                    .map(Throwable::getMessage)
                                                                                    .orElse(err.getMessage())
                                                            )
                                            )
                                            .orElseGet(() ->
                                                    r.withRequestStatus(RequestStatus.APPROVED)
                                                            .withObservations(
                                                                    (r.getObservations() == null ? "" : r.getObservations()) +
                                                                            "\nAprobada: Orden creada para coordenada (%s,%s)"
                                                                                    .formatted(r.getLatitude(), r.getLongitude())
                                                            )
                                            );

                                    var persisted = requestRepo.save(updated);
                                    sendNotificationFinalStatus(persisted);
                                })
                );

        return saved;
    }

    // ================================================================================================
    // QUERIES
    // ================================================================================================
    @Override
    public ConstructionRequestDto getById(Long id) {
        return requestRepo.findByIdWithRelations(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada: " + id));
    }

    @Override
    public List<ConstructionRequestDto> getAll() {
        return requestRepo.findAllWithRelations()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ConstructionRequestDto> getByProject(Long projectId) {
        return requestRepo.findByProjectIdWithRelations(projectId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ConstructionRequestDto> getByStatus(RequestStatus status) {
        return requestRepo.findByRequestStatusWithRelations(status)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ================================================================================================
    // MAPPER
    // ================================================================================================
    private ConstructionRequestDto toDto(ConstructionRequest e) {
        return ConstructionRequestDto.builder()
                .id(e.getId())
                .projectId(e.getProject().getId())
                .constructionTypeId(e.getConstructionType().getId())
                .latitude(e.getLatitude())
                .longitude(e.getLongitude())
                .requestDate(e.getRequestDate())
                .status(e.getRequestStatus().name())
                .observations(e.getObservations())
                .requestedById(e.getRequestedBy().getId())
                .requestedByFirstname(e.getRequestedBy().getFirstname())
                .requestedByLastname(e.getRequestedBy().getLastname())
                .build();
    }
}
