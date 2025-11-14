package com.builderssas.api.domain.model.project.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@With
public class ProjectDto {

    private Long id;
    private String name;
    private String description;

    private String createdByFullName;

    private LocalDate projectStartDate;
    private LocalDate projectEndDate;

    private Double progressPercentage;
    private String projectStatus;
}
