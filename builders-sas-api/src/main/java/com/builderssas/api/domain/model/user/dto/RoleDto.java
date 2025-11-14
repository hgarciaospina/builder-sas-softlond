package com.builderssas.api.domain.model.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * ✅ DTO que representa la información de un rol.
 * Usado en respuestas (GET) hacia el cliente.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {

    @NotNull(message = "El ID del rol no puede ser nulo.")
    private Long id;

    @NotBlank(message = "El nombre del rol es obligatorio.")
    @Size(min = 3, max = 50, message = "El nombre del rol debe tener entre 3 y 50 caracteres.")
    private String name;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres.")
    private String description;
}
