package com.builderssas.api.domain.model.material.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO de lectura para representar un tipo de material existente.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialTypeDto {

    @NotNull(message = "El ID del material no puede ser nulo.")
    private Long id;

    @NotBlank(message = "El código del material es obligatorio.")
    @Size(min = 2, max = 20, message = "El código debe tener entre 2 y 20 caracteres.")
    private String code;

    @NotBlank(message = "El nombre del material es obligatorio.")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres.")
    private String name;

    @NotBlank(message = "La unidad de medida es obligatoria.")
    @Size(min = 1, max = 10, message = "La unidad debe tener entre 1 y 10 caracteres.")
    private String unit;

    @PositiveOrZero(message = "El stock no puede ser negativo.")
    private double stock;
}
