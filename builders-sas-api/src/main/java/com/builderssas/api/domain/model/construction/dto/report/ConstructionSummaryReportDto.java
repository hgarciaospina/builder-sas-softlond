package com.builderssas.api.domain.model.construction.dto.report;

import lombok.*;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class ConstructionSummaryReportDto {

    private String constructionTypeName;
    private Long totalFinished;
    private Long totalInProgress;
    private Long totalPending;
}
