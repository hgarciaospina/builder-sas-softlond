package com.builderssas.api.services.notification;

import com.builderssas.api.domain.model.notification.NotificationDto;

import java.util.concurrent.CompletableFuture;

public interface NotificationService {

    /**
     * Método original — se mantiene sin cambios.
     */
    CompletableFuture<Void> send(NotificationDto dto);

    /**
     * MODIFICACIÓN:
     * Nuevo contrato para enviar una notificación asociada a un usuario específico.
     * No reemplaza al método original, solo lo complementa.
     */
    CompletableFuture<Void> sendForUser(NotificationDto dto, Long userId);
}
