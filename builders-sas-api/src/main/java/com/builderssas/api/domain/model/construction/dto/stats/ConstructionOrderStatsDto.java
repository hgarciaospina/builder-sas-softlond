package com.builderssas.api.domain.model.construction.dto.stats;

import com.builderssas.api.domain.model.enums.OrderStatus;
import lombok.*;

import java.util.Map;

/**
 * DTO que representa métricas globales de órdenes de construcción.
 * Únicamente contiene totales por estado.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstructionOrderStatsDto {

    /** Totales por estado. Ejemplo: {PENDING: 3, IN_PROGRESS: 5, COMPLETED: 7} */
    private Map<OrderStatus, Long> totalsByStatus;

}
