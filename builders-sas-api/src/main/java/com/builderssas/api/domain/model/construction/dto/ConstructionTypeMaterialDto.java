package com.builderssas.api.domain.model.construction.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO de lectura — se devuelve al cliente.
 * Ahora incluye:
 * ✅ quantityRequired visible
 * ✅ materialUnit (unidad del material)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstructionTypeMaterialDto {

    @NotNull(message = "El ID del registro no puede ser nulo.")
    private Long id;

    @NotNull(message = "El ID del tipo de construcción es obligatorio.")
    private Long constructionTypeId;

    @NotBlank(message = "El nombre del tipo de construcción es obligatorio.")
    private String constructionTypeName;

    @NotNull(message = "El ID del tipo de material es obligatorio.")
    private Long materialTypeId;

    @NotBlank(message = "El código del tipo de material es obligatorio.")
    private String materialTypeCode;

    @NotBlank(message = "El nombre del tipo de material es obligatorio.")
    private String materialTypeName;

    @NotBlank(message = "La unidad del material es obligatoria.")
    private String materialUnit;   // ✅ ESTE ES EL QUE FALTABA

    @NotNull(message = "La cantidad requerida no puede ser nula.")
    @Positive(message = "La cantidad requerida debe ser mayor que cero.")
    private Integer quantityRequired;
}
