package com.builderssas.api.domain.model.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.OffsetDateTime;

/**
 * ✅ DTO para asignar un rol a un usuario existente.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRoleDto {

    /** ID del usuario al que se asignará el rol. */
    @NotNull(message = "El ID del usuario es obligatorio.")
    private Long userId;

    /** ID del rol que se asignará al usuario. */
    @NotNull(message = "El ID del rol es obligatorio.")
    private Long roleId;

    /** Marca de tiempo opcional de cuándo se asigna el rol. */
    private OffsetDateTime assignedAt;
}
