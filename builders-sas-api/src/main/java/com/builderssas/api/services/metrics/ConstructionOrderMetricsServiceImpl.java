package com.builderssas.api.services.metrics;

import com.builderssas.api.domain.model.construction.dto.stats.ConstructionOrderStatsDto;
import com.builderssas.api.domain.model.construction.dto.stats.ConstructionOrderTypeStatsDto;
import com.builderssas.api.domain.model.enums.OrderStatus;
import com.builderssas.api.repository.views.StatusCountView;
import com.builderssas.api.repository.views.TypeCountView;
import com.builderssas.api.repository.views.TypeStatusCountView;
import com.builderssas.api.repository.views.ProjectStatusCountView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio final de métricas.
 * Ensambla los DTOs usados por el controlador a partir de las proyecciones
 * entregadas por AggregationService.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConstructionOrderMetricsServiceImpl implements ConstructionOrderMetricsService {

    private final ConstructionOrderMetricsAggregationService agg;

    // ============================================================
    // MÉTRICAS GLOBALES
    // ============================================================

    @Override
    public ConstructionOrderStatsDto getGlobalStats() {

        Map<OrderStatus, Long> totalsByStatus =
                agg.getGlobalStatusCounts()
                        .stream()
                        .collect(Collectors.toMap(
                                StatusCountView::getStatus,
                                StatusCountView::getCount
                        ));

        return ConstructionOrderStatsDto.builder()
                .totalsByStatus(totalsByStatus)
                .build();
    }

    @Override
    public List<ConstructionOrderTypeStatsDto> getGlobalTypeStats() {

        List<TypeCountView> totals = agg.getGlobalTypeCounts();
        List<TypeStatusCountView> breakdown = agg.getGlobalTypeStatusCounts();

        Map<Long, Map<OrderStatus, Long>> statusMap =
                breakdown.stream()
                        .collect(Collectors.groupingBy(
                                TypeStatusCountView::getConstructionTypeId,
                                Collectors.toMap(
                                        TypeStatusCountView::getStatus,
                                        TypeStatusCountView::getCount,
                                        Long::sum
                                )
                        ));

        return totals.stream()
                .map(t -> ConstructionOrderTypeStatsDto.builder()
                        .constructionTypeId(t.getConstructionTypeId())
                        .constructionTypeName(t.getConstructionTypeName())
                        .totalOrders(t.getTotal())
                        .statusBreakdown(statusMap.getOrDefault(
                                t.getConstructionTypeId(), Map.of()
                        ))
                        .build())
                .sorted(Comparator.comparing(ConstructionOrderTypeStatsDto::getConstructionTypeName))
                .toList();
    }


    // ============================================================
    // MÉTRICAS POR PROYECTO
    // ============================================================

    @Override
    public List<ConstructionOrderTypeStatsDto> getProjectTypeStats(Long projectId) {

        List<TypeCountView> totals = agg.getProjectTypeCounts(projectId);
        List<TypeStatusCountView> breakdown = agg.getProjectTypeStatusCounts(projectId);

        Map<Long, Map<OrderStatus, Long>> statusMap =
                breakdown.stream()
                        .collect(Collectors.groupingBy(
                                TypeStatusCountView::getConstructionTypeId,
                                Collectors.toMap(
                                        TypeStatusCountView::getStatus,
                                        TypeStatusCountView::getCount,
                                        Long::sum
                                )
                        ));

        return totals.stream()
                .map(t -> ConstructionOrderTypeStatsDto.builder()
                        .constructionTypeId(t.getConstructionTypeId())
                        .constructionTypeName(t.getConstructionTypeName())
                        .totalOrders(t.getTotal())
                        .statusBreakdown(statusMap.getOrDefault(
                                t.getConstructionTypeId(), Map.of()
                        ))
                        .build())
                .sorted(Comparator.comparing(ConstructionOrderTypeStatsDto::getConstructionTypeName))
                .toList();
    }
}
