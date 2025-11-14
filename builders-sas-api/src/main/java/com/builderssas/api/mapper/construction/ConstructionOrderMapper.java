package com.builderssas.api.mapper.construction;

import com.builderssas.api.domain.model.construction.ConstructionOrder;
import com.builderssas.api.domain.model.constructionorder.dto.ConstructionOrderDto;
import org.springframework.stereotype.Component;

@Component
public class ConstructionOrderMapper {

    public ConstructionOrderDto toDto(ConstructionOrder e) {

        return ConstructionOrderDto.builder()
                .id(e.getId())

                .projectId(e.getProject().getId())
                .projectName(e.getProject().getName())

                .constructionTypeId(e.getConstructionType().getId())
                .constructionTypeName(e.getConstructionType().getName())

                .requestedByUserId(e.getRequestedBy().getId())
                .requestedByFirstname(e.getRequestedBy().getFirstname())
                .requestedByLastname(e.getRequestedBy().getLastname())

                .latitude(e.getLatitude())
                .longitude(e.getLongitude())

                .scheduledStartDate(e.getScheduledStartDate())
                .scheduledEndDate(e.getScheduledEndDate())

                .orderStatus(e.getOrderStatus().name())

                .observations(e.getObservations())

                // si es null → Angular recibirá null, no rompe nada
                .materialsConsumption(e.getMaterialsConsumption())

                .build();
    }
}
