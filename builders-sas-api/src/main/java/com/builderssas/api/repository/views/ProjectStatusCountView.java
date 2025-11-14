package com.builderssas.api.repository.views;

import com.builderssas.api.domain.model.enums.OrderStatus;

/**
 * Proyección para conteo de órdenes por estado dentro de un proyecto específico.
 */
public interface ProjectStatusCountView {
    OrderStatus getStatus();
    Long getCount();
}
