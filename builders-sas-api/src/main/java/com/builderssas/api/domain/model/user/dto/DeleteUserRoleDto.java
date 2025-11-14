package com.builderssas.api.domain.model.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * ✅ DTO para eliminar un registro de relación Usuario–Rol.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserRoleDto {

    /** Identificador primario del UserRole a eliminar. */
    @NotNull(message = "El ID del registro UserRole es obligatorio para eliminarlo.")
    private Long id;
}
