package com.builderssas.api.services.metrics;

import com.builderssas.api.repository.ConstructionOrderRepository;
import com.builderssas.api.repository.views.StatusCountView;
import com.builderssas.api.repository.views.TypeCountView;
import com.builderssas.api.repository.views.TypeStatusCountView;
import com.builderssas.api.repository.views.ProjectStatusCountView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación que delega directamente al repositorio.
 * No aplica transformaciones: solo expone las proyecciones tal cual.
 */
@Service
@RequiredArgsConstructor
public class ConstructionOrderMetricsAggregationServiceImpl
        implements ConstructionOrderMetricsAggregationService {

    private final ConstructionOrderRepository repo;

    // ==== Métricas globales ====

    @Override
    public List<StatusCountView> getGlobalStatusCounts() {
        return repo.countAllGroupedByStatus();
    }

    @Override
    public List<TypeCountView> getGlobalTypeCounts() {
        return repo.countAllGroupedByConstructionType();
    }

    @Override
    public List<TypeStatusCountView> getGlobalTypeStatusCounts() {
        return repo.countAllGroupedByConstructionTypeAndStatus();
    }


    // ==== Métricas por proyecto ====

    @Override
    public List<ProjectStatusCountView> getProjectStatusCounts(Long projectId) {
        return repo.countByProjectGroupedByStatus(projectId);
    }

    @Override
    public List<TypeCountView> getProjectTypeCounts(Long projectId) {
        return repo.countByProjectGroupedByConstructionType(projectId);
    }

    @Override
    public List<TypeStatusCountView> getProjectTypeStatusCounts(Long projectId) {
        return repo.countByProjectGroupedByConstructionTypeAndStatus(projectId);
    }
}
