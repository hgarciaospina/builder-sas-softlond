package com.builderssas.api.domain.model.notification;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO universal para notificaciones Webhook.
 * Recibido directamente por Angular.
 *
 * MODIFICACIÓN:
 * Se agrega userId para permitir notificaciones por usuario,
 * sin afectar la compatibilidad con el pipeline original.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private String eventType;        // REQUEST_APPROVED, ORDER_CREATED, etc.
    private LocalDateTime timestamp;
    private Object payload;          // Información de la solicitud u orden

    /* MODIFICACIÓN — requerido para storage por usuario */
    private Long userId;
}
