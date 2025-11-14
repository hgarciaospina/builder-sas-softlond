package com.builderssas.api.domain.model.construction.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * =============================================================================
 *  DTO — CREACIÓN DE SOLICITUD DE CONSTRUCCIÓN
 * =============================================================================
 *
 * Este DTO representa los datos que el frontend envía para registrar una nueva
 * solicitud de construcción dentro de un proyecto.
 *
 * Contiene únicamente los campos necesarios para CREAR la solicitud.
 *
 * Reglas:
 *  - Todos los campos son obligatorios.
 *  - Las coordenadas representan la posición dentro del proyecto.
 *  - requestedByUserId indica el usuario (arquitecto) que registra la solicitud.
 *  - La validación automática (aprobado/rechazado) se realiza en el servicio.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConstructionRequestDto {

    /** ID del proyecto donde se quiere construir. */
    @NotNull(message = "El ID del proyecto es obligatorio.")
    private Long projectId;

    /** ID del tipo de construcción (casa, lago, edificio, etc.). */
    @NotNull(message = "El tipo de construcción es obligatorio.")
    private Long constructionTypeId;

    /** Coordenada en latitud. */
    @NotNull(message = "La latitud es obligatoria.")
    private Double latitude;

    /** Coordenada en longitud. */
    @NotNull(message = "La longitud es obligatoria.")
    private Double longitude;

    /** Usuario (arquitecto) que registra la solicitud. */
    @NotNull(message = "El usuario solicitante es obligatorio.")
    private Long requestedByUserId;
}
