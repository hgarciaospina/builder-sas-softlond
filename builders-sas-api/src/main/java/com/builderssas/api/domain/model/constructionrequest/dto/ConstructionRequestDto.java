package com.builderssas.api.domain.model.constructionrequest.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConstructionRequestDto {

    private Long id;

    private Long projectId;
    private Long constructionTypeId;

    private Double latitude;
    private Double longitude;

    private LocalDate requestDate;

    private String status;          // APPROVED / REJECTED / FAILED
    private String observations;     // Mensajes generados automáticamente

    // Datos del solicitante
    private Long requestedById;
    private String requestedByFirstname;
    private String requestedByLastname;
}
