package com.builderssas.api.domain.model.project.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@With
public class UpdateProjectDto {

    @NotBlank(message = "El nombre del proyecto es obligatorio.")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres.")
    private String name;

    @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres.")
    private String description;
}
