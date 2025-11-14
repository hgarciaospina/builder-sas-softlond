package com.builderssas.api.domain.model.construction.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO para actualizar un tipo de construcción existente.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConstructionTypeDto {

        @NotBlank(message = "El nombre del tipo de construcción es obligatorio.")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres.")
        private String name;

        @NotNull(message = "La duración en días es obligatoria.")
        @Positive(message = "La duración debe ser un número positivo.")
        private Integer durationDays;
}
