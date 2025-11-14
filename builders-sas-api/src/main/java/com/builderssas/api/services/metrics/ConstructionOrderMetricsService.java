package com.builderssas.api.services.metrics;

import com.builderssas.api.domain.model.construction.dto.stats.ConstructionOrderStatsDto;
import com.builderssas.api.domain.model.construction.dto.stats.ConstructionOrderTypeStatsDto;

import java.util.List;

/**
 * Servicio encargado de construir DTOs finales de métricas.
 */
public interface ConstructionOrderMetricsService {

    ConstructionOrderStatsDto getGlobalStats();

    List<ConstructionOrderTypeStatsDto> getGlobalTypeStats();

    List<ConstructionOrderTypeStatsDto> getProjectTypeStats(Long projectId);
}
