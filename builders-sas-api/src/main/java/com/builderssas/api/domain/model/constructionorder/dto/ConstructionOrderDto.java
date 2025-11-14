package com.builderssas.api.domain.model.constructionorder.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConstructionOrderDto {

    private Long id;

    private Long projectId;
    private String projectName;

    private Long constructionTypeId;
    private String constructionTypeName;

    private Long requestedByUserId;
    private String requestedByFirstname;
    private String requestedByLastname;

    private Double latitude;
    private Double longitude;

    private LocalDate scheduledStartDate;
    private LocalDate scheduledEndDate;

    private String orderStatus;

    private String observations;

    private List<MaterialConsumptionDto> materialsConsumption;

}
