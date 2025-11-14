package com.builderssas.api.domain.model.enums;

/**
 * Estados posibles de una orden de construcción.
 *
 * - PENDING: la orden fue creada y tiene una fecha de inicio programada.
 * - IN_PROGRESS: el cron AM inicia automáticamente la construcción.
 * - FINISHED: el cron PM marca la construcción como finalizada al cumplir su duración.
 */
public enum OrderStatus {
    PENDING,
    IN_PROGRESS,
    FINISHED
}
