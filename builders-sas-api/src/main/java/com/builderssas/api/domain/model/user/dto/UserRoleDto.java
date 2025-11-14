package com.builderssas.api.domain.model.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.OffsetDateTime;

/**
 * ✅ DTO para representar los detalles de la relación Usuario–Rol.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDto {

    /** Identificador único del registro usuario–rol. */
    @NotNull(message = "El ID del registro usuario–rol no puede ser nulo.")
    private Long id;

    /** ID del usuario asociado a este rol. */
    @NotNull(message = "El ID del usuario es obligatorio.")
    private Long userId;

    /** Nombre de usuario (opcional para conveniencia). */
    private String username;

    /** ID del rol asignado al usuario. */
    @NotNull(message = "El ID del rol es obligatorio.")
    private Long roleId;

    /** Nombre del rol (opcional para conveniencia). */
    private String roleName;

    /** Fecha/hora de asignación (opcional). */
    private OffsetDateTime assignedAt;
}
