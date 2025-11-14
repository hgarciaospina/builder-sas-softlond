package com.builderssas.api.domain.model.construction.dto.report;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class OrdersByStatusReportDto {

    private Long orderId;
    private String projectName;
    private String constructionTypeName;

    private Double latitude;
    private Double longitude;

    private LocalDate scheduledStartDate;
    private LocalDate scheduledEndDate;

    private String orderStatus;
}
