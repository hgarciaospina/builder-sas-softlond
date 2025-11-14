package com.builderssas.api.domain.model.enums;

/**
 * Estados posibles de un proyecto.
 *
 * - IN_PROGRESS: el proyecto inició su ejecución cuando la primera orden comenzó.
 * - FINISHED: todas las órdenes asociadas finalizaron correctamente.
 */
public enum ProjectStatus {
    PLANNED,
    IN_PROGRESS,
}
