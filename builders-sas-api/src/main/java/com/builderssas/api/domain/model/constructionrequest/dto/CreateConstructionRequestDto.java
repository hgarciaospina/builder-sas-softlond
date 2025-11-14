package com.builderssas.api.domain.model.constructionrequest.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateConstructionRequestDto {

    private Long projectId;              // Proyecto al que pertenece la solicitud
    private Long constructionTypeId;     // Tipo de construcción
    private Double latitude;             // Coordenada X real
    private Double longitude;            // Coordenada Y real
}
