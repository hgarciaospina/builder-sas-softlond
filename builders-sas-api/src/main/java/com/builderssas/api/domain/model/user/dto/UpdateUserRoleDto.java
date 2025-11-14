package com.builderssas.api.domain.model.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.OffsetDateTime;

/**
 * ✅ DTO para actualizar una relación existente entre usuario y rol.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRoleDto {

    /** ID del usuario (opcional para actualización parcial). */
    @Positive(message = "Debe seleccionar un usuario existente.")
    private Long userId;

    /** ID del rol (opcional para actualización parcial). */
    @Positive(message = "El rol dee existir.")
    private Long roleId;

    /** Fecha/hora de asignación (opcional). */
    private OffsetDateTime assignedAt;
}
