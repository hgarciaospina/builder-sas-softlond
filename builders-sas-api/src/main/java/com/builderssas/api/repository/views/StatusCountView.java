package com.builderssas.api.repository.views;

import com.builderssas.api.domain.model.enums.OrderStatus;

/**
 * Proyección para conteo global de órdenes por estado.
 */
public interface StatusCountView {
    OrderStatus getStatus();
    Long getCount();
}
