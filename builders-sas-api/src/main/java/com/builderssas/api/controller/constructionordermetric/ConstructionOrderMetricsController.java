package com.builderssas.api.controller.constructionordermetric;

import com.builderssas.api.repository.ConstructionOrderRepository;
import com.builderssas.api.repository.views.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/construction-orders/metrics")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ConstructionOrderMetricsController {

    private final ConstructionOrderRepository orderRepo;

    // ============================================================
    //  MÉTRICAS GLOBALES
    // ============================================================

    /** Total por estado global */
    @GetMapping("/status")
    public List<StatusCountView> getAllByStatus() {
        return orderRepo.countAllGroupedByStatus();
    }

    /** Total por tipo global */
    @GetMapping("/by-type")
    public List<TypeCountView> getAllByType() {
        return orderRepo.countAllGroupedByConstructionType();
    }

    /** Total por tipo + estado global */
    @GetMapping("/by-type-status")
    public List<TypeStatusCountView> getAllByTypeAndStatus() {
        return orderRepo.countAllGroupedByConstructionTypeAndStatus();
    }


    // ============================================================
    // MÉTRICAS POR PROYECTO
    // ============================================================

    /** Por proyecto → por estado */
    @GetMapping("/by-project/{projectId}/status")
    public List<ProjectStatusCountView> getProjectByStatus(@PathVariable Long projectId) {
        return orderRepo.countByProjectGroupedByStatus(projectId);
    }

    /** Por proyecto → por tipo */
    @GetMapping("/by-project/{projectId}/by-type")
    public List<TypeCountView> getProjectByType(@PathVariable Long projectId) {
        return orderRepo.countByProjectGroupedByConstructionType(projectId);
    }

    /** Por proyecto → por tipo + estado */
    @GetMapping("/by-project/{projectId}/by-type-status")
    public List<TypeStatusCountView> getProjectByTypeAndStatus(@PathVariable Long projectId) {
        return orderRepo.countByProjectGroupedByConstructionTypeAndStatus(projectId);
    }
}
