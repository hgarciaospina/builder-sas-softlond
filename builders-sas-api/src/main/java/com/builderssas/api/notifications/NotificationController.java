package com.builderssas.api.notifications;

import com.builderssas.api.domain.model.notification.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para notificaciones.
 *
 * MODIFICACIÓN:
 * Se agregan endpoints por usuario SIN eliminar ni modificar
 * el comportamiento original global.
 */
@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationStorage storage;

    /**
     * Método original.
     * Se conserva exactamente igual.
     */
    @GetMapping
    public List<NotificationDto> getAll() {
        List<NotificationDto> list = storage.getAll();
        log.info("📡 Notificaciones globales retornadas: {}", list.size());
        return list;
    }

    /**
     * MODIFICACIÓN:
     * Nuevo endpoint para obtener notificaciones filtradas por usuario.
     * Se agrega sin alterar el comportamiento previo.
     */
    @GetMapping("/by-user")
    public List<NotificationDto> getByUser(@RequestParam Long userId) {
        List<NotificationDto> list = storage.getForUser(userId);
        return list;
    }

    /**
     * Método original.
     * Se conserva sin cambios.
     */
    @DeleteMapping
    public void clear() {
        storage.clear();
        log.warn("🗑️ Se limpiaron todas las notificaciones globales");
    }

    /**
     * MODIFICACIÓN:
     * Nuevo endpoint para limpiar las notificaciones de un usuario específico.
     * Agregado sin alterar clear().
     */
    @DeleteMapping("/by-user")
    public void clearForUser(@RequestParam Long userId) {
        storage.clearForUser(userId);
        log.warn("🗑️ Se limpiaron las notificaciones del usuario {}", userId);
    }
}
