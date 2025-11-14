package com.builderssas.api.domain.model.construction.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * ✅ DTO de actualización para ConstructionTypeMaterial
 * Solo permite modificar la cantidad requerida.
 * No exige materialTypeId ni constructionTypeId.
 * Mantiene los tipos EXACTOS del modelo (Integer).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConstructionTypeMaterialDto {

    @NotNull(message = "La cantidad requerida es obligatoria.")
    @Min(value = 1, message = "La cantidad requerida debe ser mayor o igual a 1.")
    private Integer quantityRequired;
}
