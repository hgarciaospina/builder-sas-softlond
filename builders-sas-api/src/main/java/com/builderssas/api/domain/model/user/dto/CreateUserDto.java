package com.builderssas.api.domain.model.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

/**
 * ✅ DTO para crear un nuevo usuario.
 * Incluye validaciones de formato y campos obligatorios.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {

    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres.")
    private String username;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres.")
    private String firstname;

    @NotBlank(message = "El apellido es obligatorio.")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres.")
    private String lastname;

    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "El correo electrónico debe tener un formato válido.")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String password;

    @NotNull(message = "El estado de activación es obligatorio.")
    private Boolean active;

    @NotEmpty(message = "Debe asignar al menos un rol al usuario.")
    private Set<@NotNull(message = "El ID del rol no puede ser nulo.") Long> roleIds;
}
