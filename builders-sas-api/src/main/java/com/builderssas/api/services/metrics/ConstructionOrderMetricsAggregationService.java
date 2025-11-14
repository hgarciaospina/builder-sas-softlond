package com.builderssas.api.services.metrics;

import com.builderssas.api.repository.views.StatusCountView;
import com.builderssas.api.repository.views.TypeCountView;
import com.builderssas.api.repository.views.TypeStatusCountView;
import com.builderssas.api.repository.views.ProjectStatusCountView;

import java.util.List;

/**
 * Servicio que expone directamente las proyecciones del repositorio
 * sin aplicar transformaciones. Se mantiene separado para evitar
 * mezclar lógica de agregación con la construcción de DTOs.
 */
public interface ConstructionOrderMetricsAggregationService {

    // ==== Métricas globales ====

    /** Delegación directa a ConstructionOrderRepository#countAllGroupedByStatus() */
    List<StatusCountView> getGlobalStatusCounts();

    /** Delegación directa a ConstructionOrderRepository#countAllGroupedByConstructionType() */
    List<TypeCountView> getGlobalTypeCounts();

    /** Delegación directa a ConstructionOrderRepository#countAllGroupedByConstructionTypeAndStatus() */
    List<TypeStatusCountView> getGlobalTypeStatusCounts();


    // ==== Métricas por proyecto ====

    /** Delegación directa a ConstructionOrderRepository#countByProjectGroupedByStatus(Long) */
    List<ProjectStatusCountView> getProjectStatusCounts(Long projectId);

    /** Delegación directa a ConstructionOrderRepository#countByProjectGroupedByConstructionType(Long) */
    List<TypeCountView> getProjectTypeCounts(Long projectId);

    /** Delegación directa a ConstructionOrderRepository#countByProjectGroupedByConstructionTypeAndStatus(Long) */
    List<TypeStatusCountView> getProjectTypeStatusCounts(Long projectId);
}
