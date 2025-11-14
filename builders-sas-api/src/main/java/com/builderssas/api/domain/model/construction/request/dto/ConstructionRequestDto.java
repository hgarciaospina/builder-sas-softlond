package com.builderssas.api.domain.model.construction.request.dto;

import com.builderssas.api.domain.model.enums.RequestStatus;
import lombok.*;

import java.time.LocalDate;

/**
 * =============================================================================
 *  DTO — REPRESENTACIÓN DE UNA SOLICITUD DE CONSTRUCCIÓN
 * =============================================================================
 *
 * Este DTO se usa para:
 *  - Consultar solicitudes individuales
 *  - Listar solicitudes por proyecto
 *  - Listar solicitudes por estado
 *  - Exponer datos a la UI de manera estructurada
 *
 * Contiene:
 *  - IDs relevantes
 *  - Coordenadas
 *  - Usuario solicitante
 *  - Fechas y estado
 *  - Observaciones generadas automáticamente por el sistema
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstructionRequestDto {

    /** ID de la solicitud. */
    private Long id;

    /** Proyecto al que pertenece. */
    private Long projectId;

    /** ID del tipo de construcción solicitada. */
    private Long constructionTypeId;

    /** Nombre del tipo de construcción (para mostrar en la UI). */
    private String constructionTypeName;

    /** Coordenada de latitud. */
    private Double latitude;

    /** Coordenada de longitud. */
    private Double longitude;

    /** Usuario que registró la solicitud. */
    private Long requestedByUserId;

    /** Nombre del usuario solicitante. */
    private String requestedByUserName;

    /** Fecha en que se registró la solicitud. */
    private LocalDate requestDate;

    /** Estado actual de la solicitud. */
    private RequestStatus requestStatus;

    /** Mensajes generados automáticamente (rechazo, aprobación, etc.) */
    private String observations;
}
