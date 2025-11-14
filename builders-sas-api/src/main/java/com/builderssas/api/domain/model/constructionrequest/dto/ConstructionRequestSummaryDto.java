package com.builderssas.api.domain.model.constructionrequest.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConstructionRequestSummaryDto {

    private Long requestId;

    private Long projectId;
    private String projectName;

    private Long constructionTypeId;
    private String constructionTypeName;

    private Double latitude;
    private Double longitude;

    private LocalDate requestDate;

    private String requestedByFirstname;
    private String requestedByLastname;

    private String status;  // APPROVED / REJECTED / FAILED
}
