package com.builderssas.api.notifications;

import com.builderssas.api.domain.model.notification.NotificationDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * NotificationStorage
 *
 * Esta clase originalmente almacenaba todas las notificaciones en una sola lista global.
 * Para permitir notificaciones por usuario, se agrega un almacenamiento adicional usando
 * un Map<Long, List<NotificationDto>> sin eliminar el almacenamiento original.
 *
 * Esto mantiene compatibilidad hacia atrás y permite incorporar el nuevo comportamiento
 * sin afectar ninguna otra parte del sistema.
 */
@Component
public class NotificationStorage {

    /**
     * Almacenamiento original (global).
     * No se elimina para garantizar compatibilidad.
     */
    private final List<NotificationDto> list = new CopyOnWriteArrayList<>();

    /**
     * MODIFICACIÓN:
     * Nuevo almacenamiento por usuario sin eliminar el global.
     */
    private final Map<Long, List<NotificationDto>> userStorage = new ConcurrentHashMap<>();

    /**
     * Método original. Sin cambios.
     */
    public void add(NotificationDto dto) {
        list.add(dto);
    }

    /**
     * MODIFICACIÓN:
     * Guardar notificación asociada a un usuario específico.
     */
    public void addForUser(Long userId, NotificationDto dto) {
        userStorage
                .computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>())
                .add(dto);
    }

    /**
     * Método original. Sin cambios.
     */
    public List<NotificationDto> getAll() {
        return List.copyOf(list);
    }

    /**
     * MODIFICACIÓN:
     * Retornar COPIA INMUTABLE de la lista de ese usuario
     * para evitar exposición del almacenamiento interno.
     */
    public List<NotificationDto> getForUser(Long userId) {
        return userStorage.containsKey(userId)
                ? List.copyOf(userStorage.get(userId))
                : List.of();
    }

    /**
     * Método original. Sin cambios.
     */
    public void clear() {
        list.clear();
    }

    /**
     * MODIFICACIÓN:
     * Eliminar todas las notificaciones de un usuario específico.
     */
    public void clearForUser(Long userId) {
        userStorage.remove(userId);
    }
}
