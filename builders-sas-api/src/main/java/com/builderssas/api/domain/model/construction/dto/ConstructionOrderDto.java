package com.builderssas.api.domain.model.construction.dto;

import com.builderssas.api.domain.model.enums.OrderStatus;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO de sólo lectura para exponer información de una Orden de Construcción.
 *
 * Este DTO está pensado para listados, reportes y consultas del frontend.
 * No se usa para creación/actualización porque las órdenes son gestionadas por el CRON.
 *
 * Campos expuestos:
 * - Identificadores de relaciones: projectId, constructionTypeId, requestedByUserId, constructionRequestId
 * - Atributos de navegación útiles para UI: projectName, constructionTypeName, requestedByFullName
 * - Coordenadas y fechas programadas
 * - Estado actual de la orden
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@With
public class ConstructionOrderDto {

    /** ID de la orden. */
    private Long id;

    /** ID del proyecto al que pertenece la orden. */
    private Long projectId;

    /** Nombre del proyecto (ayuda de navegación para UI). */
    private String projectName;

    /** ID del tipo de construcción asociado a la orden. */
    private Long constructionTypeId;

    /** Nombre del tipo de construcción (ayuda de navegación para UI). */
    private String constructionTypeName;

    /** ID del usuario que registró la solicitud original. */
    private Long requestedByUserId;

    /** Nombre completo del usuario solicitante (ayuda de navegación para UI). */
    private String requestedByFullName;

    /** ID de la solicitud de construcción que originó la orden. */
    private Long constructionRequestId;

    /** Latitud de la orden. */
    private Double latitude;

    /** Longitud de la orden. */
    private Double longitude;

    /** Fecha en la que se solicitó originalmente la construcción (desde la solicitud). */
    private LocalDate requestedDate;

    /** Fecha programada de inicio (calculada por el proceso). */
    private LocalDate scheduledStartDate;

    /** Fecha programada de fin (calculada por el proceso). */
    private LocalDate scheduledEndDate;

    /** Estado actual de la orden. */
    private OrderStatus orderStatus;
}
