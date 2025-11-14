package com.builderssas.api.domain.model.constructionorder.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialConsumptionDto {

    private String materialName;

    private Double stockBefore;

    private Double required;

    private Double stockAfter;

}
