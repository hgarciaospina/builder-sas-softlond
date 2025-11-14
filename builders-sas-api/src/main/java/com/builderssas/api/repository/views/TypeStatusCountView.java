package com.builderssas.api.repository.views;

import com.builderssas.api.domain.model.enums.OrderStatus;

/**
 * Proyección para conteo por tipo de construcción y estado.
 * Usada globalmente y filtrada por proyecto.
 */
public interface TypeStatusCountView {
    Long getConstructionTypeId();
    String getConstructionTypeName();
    OrderStatus getStatus();
    Long getCount();
}
