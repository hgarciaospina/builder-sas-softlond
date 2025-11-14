package com.builderssas.api.domain.model.construction.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO for creating a ConstructionTypeMaterial entity.
 * La cantidad requerida por defecto es 1.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConstructionTypeMaterialDto {

    @NotNull(message = "El ID del tipo de construcción es obligatorio.")
    private Long constructionTypeId;

    @NotNull(message = "El ID del tipo de material es obligatorio.")
    private Long materialTypeId;

    @Positive(message = "La cantidad requerida debe ser mayor que cero.")
    @Builder.Default
    private Integer quantityRequired = 1;
}
