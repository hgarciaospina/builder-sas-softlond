package com.builderssas.api.domain.model.enums;

/**
 * Estados posibles de una solicitud de construcción.
 *
 * - PENDING: creada por el arquitecto, lista para ser procesada por el sistema.
 * - APPROVED: validada; coordenadas libres y materiales suficientes; orden creada.
 * - REJECTED: inválida (coordenada ocupada / materiales insuficientes).
 * - FAILED: error interno al intentar procesarla.
 */
public enum RequestStatus {
    PENDING,
    APPROVED,
    REJECTED,
    FAILED
}
