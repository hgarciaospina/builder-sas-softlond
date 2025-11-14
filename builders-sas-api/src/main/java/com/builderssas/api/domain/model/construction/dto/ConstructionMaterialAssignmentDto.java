package com.builderssas.api.domain.model.construction.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

/**
 * DTO for assigning multiple MaterialTypes to a ConstructionType.
 * Used in the asynchronous functional creation flow.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstructionMaterialAssignmentDto {

    @NotNull(message = "Tipo de construcción es obligatorio")
    private Long constructionTypeId;

    @NotEmpty(message = "Debe enviar al menos un material")
    @Valid
    private List<MaterialAssignment> materials;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaterialAssignment {

        @NotNull(message = "Tipo de material es obligatorio")
        private Long materialTypeId;

        @NotNull(message = "Cantidad de material es obligatorio")
        @Min(value = 1, message = "quantityRequired debe ser mayor que cero")
        private Integer quantityRequired;
    }
}
