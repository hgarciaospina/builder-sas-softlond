package com.builderssas.api.domain.model.construction.dto.stats;

import com.builderssas.api.domain.model.enums.OrderStatus;
import lombok.*;

import java.util.Map;

/**
 * DTO que representa métricas por tipo de construcción.
 * Incluye ID, nombre, total y desglose por estado.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstructionOrderTypeStatsDto {

    /** ID del tipo de construcción. */
    private Long constructionTypeId;

    /** Nombre del tipo de construcción. */
    private String constructionTypeName;

    /** Total de órdenes registradas para este tipo. */
    private Long totalOrders;

    /** Desglose por estado. Ejemplo: {COMPLETED: 4, IN_PROGRESS: 1} */
    private Map<OrderStatus, Long> statusBreakdown;

}
