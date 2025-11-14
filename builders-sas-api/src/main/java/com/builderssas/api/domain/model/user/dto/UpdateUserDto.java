package com.builderssas.api.domain.model.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

/**
 * ✅ DTO para actualizar un usuario existente.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {

    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres.")
    private String username;

    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres.")
    private String firstname;

    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres.")
    private String lastname;

    @Email(message = "El correo electrónico debe tener un formato válido.")
    private String email;

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String password;

    private Boolean active;

    @NotEmpty(message = "Debe asignar al menos un rol al usuario.")
    private Set<@NotNull(message = "El ID del rol no puede ser nulo.") Long> roleIds;
}
