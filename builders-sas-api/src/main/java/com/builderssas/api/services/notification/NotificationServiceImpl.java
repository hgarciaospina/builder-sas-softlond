package com.builderssas.api.services.notification;

import com.builderssas.api.config.NotificationConfig;
import com.builderssas.api.domain.model.notification.NotificationDto;
import com.builderssas.api.notifications.NotificationStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Servicio encargado del flujo completo de notificaciones:
 *
 *  enrich → send → persist
 expone sendForUser() para agregar un userId a la notificación
 * sin alterar el flujo original.
 */

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationConfig config;
    private final NotificationStorage storage;
    private final RestTemplate rest;
    private final RetryTemplate retryTemplate;

    private final Function<NotificationDto, NotificationDto> enrich;
    private final Function<NotificationDto, NotificationDto> send;
    private final Function<NotificationDto, NotificationDto> persist;

    /**
     * Constructor de NotificationServiceImpl.
     *
     * @param config configuración del webhook
     * @param storage almacenamiento de notificaciones
     * @param retryTemplate plantilla de reintentos (inyectada desde RetryConfig)
     */
    public NotificationServiceImpl(
            NotificationConfig config,
            NotificationStorage storage,
            RetryTemplate retryTemplate
    ) {
        this.config = config;
        this.storage = storage;
        this.rest = new RestTemplate();
        this.retryTemplate = retryTemplate;

        log.info("Webhook URL cargada: {}", config.getWebhookUrl());

        // ===========================================
        // ENRICH — Enriquecimiento funcional original
        // ===========================================
        this.enrich = dto ->
                Optional.ofNullable(dto)
                        .map(d -> NotificationDto.builder()
                                .eventType(d.getEventType())
                                .timestamp(LocalDateTime.now())
                                .payload(d.getPayload())
                                .userId(d.getUserId())
                                .build()
                        )
                        .orElseThrow(() -> new IllegalArgumentException("Notificación inválida"));

        // ===========================================
        // SEND — Envío externo funcional original
        // ===========================================
        this.send = enriched ->
                Optional.of(enriched)
                        .map(d -> postSafely(d, config.getWebhookUrl()))
                        .orElse(enriched);

        // ===========================================
        // PERSIST — Guardado local funcional original
        // ===========================================
        this.persist = dto ->
                Optional.of(dto)
                        .map(this::storeSafely)
                        .orElse(dto);
    }

    /**
     * Envia una notificación global (flujo original).
     *
     * @param dto notificación original
     * @return CompletableFuture del proceso
     */
    @Override
    public CompletableFuture<Void> send(NotificationDto dto) {
        return CompletableFuture
                .supplyAsync(() -> enrich.apply(dto))
                .thenApply(send)
                .thenApply(persist)
                .thenAccept(n -> log.debug("Flujo completado [{}]", n.getEventType()))
                .exceptionally(ex -> {
                    log.error("Error en pipeline de notificación: {}", ex.getMessage());
                    return null;
                });
    }

    // ============================================================
    //     🔥🔥🔥  postSafely — Envío con RetryTemplate (FUNCIONAL)
    // ============================================================

    /**
     * Envía una notificación al webhook externo con reintentos automáticos.
     * No introduce imperativo, no usa try/catch y no modifica el pipeline.
     *
     * @param dto notificación original
     * @param url URL del webhook
     * @return DTO resultante después de aplicar el retry
     */
    private NotificationDto postSafely(NotificationDto dto, String url) {

        return Optional.of(dto)
                .map(original ->
                        retryTemplate.execute(
                                // Acción con reintento automático
                                context -> {
                                    String response = rest.postForObject(url, original, String.class);
                                    log.info("Webhook OK (intento {}): {}", context.getRetryCount() + 1, response);

                                    return NotificationDto.builder()
                                            .eventType(original.getEventType())
                                            .timestamp(original.getTimestamp())
                                            .payload(original.getPayload())
                                            .userId(original.getUserId())
                                            .build();
                                },
                                // Recuperación final sin excepciones
                                context -> {
                                    log.warn("Webhook FAILED después de {} intentos",
                                            context.getRetryCount() + 1);

                                    return NotificationDto.builder()
                                            .eventType(original.getEventType())
                                            .timestamp(original.getTimestamp())
                                            .payload("Error al enviar notificación")
                                            .userId(original.getUserId())
                                            .build();
                                }
                        )
                )
                .orElseThrow(() -> new IllegalArgumentException("Notificación inválida"));
    }

    /**
     * Guardado local de notificación (flujo original).
     *
     * @param dto notificación
     * @return la misma notificación
     */
    private NotificationDto storeSafely(NotificationDto dto) {
        storage.add(dto);
        return dto;
    }

    /**
     * Envía una notificación asociada a un usuario específico.
     *
     * @param dto notificación original
     * @param userId ID del usuario destino
     * @return CompletableFuture del proceso
     */
    public CompletableFuture<Void> sendForUser(NotificationDto dto, Long userId) {
        return CompletableFuture
                .supplyAsync(() -> enrich.apply(dto))
                .thenApply(send)
                .thenApply(n -> {
                    NotificationDto enrichedWithUser =
                            NotificationDto.builder()
                                    .eventType(n.getEventType())
                                    .timestamp(n.getTimestamp())
                                    .payload(n.getPayload())
                                    .userId(userId)
                                    .build();

                    return persistForUser(enrichedWithUser, userId);
                })
                .thenAccept(n -> log.debug("Flujo por usuario completado [{}] para userId={}", n.getEventType(), userId))
                .exceptionally(ex -> {
                    log.error("Error en notificación por usuario: {}", ex.getMessage());
                    return null;
                });
    }

    /**
     * Guarda una notificación dirigida a un usuario específico.
     *
     * @param dto notificación con userId
     * @param userId id del usuario
     * @return notificación persistida
     */
    private NotificationDto persistForUser(NotificationDto dto, Long userId) {
        log.warn("VAMPI LOG — Guardando notificación para userId={} → evento={}, payload={}",
                userId, dto.getEventType(), dto.getPayload());

        storage.addForUser(userId, dto);
        return dto;
    }

    /**
     * Log auxiliar usado por el pipeline original.
     *
     * @param dto notificación
     * @param msg texto del log
     * @return la misma notificación
     */
    private NotificationDto logAndReturn(NotificationDto dto, String msg) {
        log.info("{} [{}]", msg, dto.getEventType());
        return dto;
    }
}
