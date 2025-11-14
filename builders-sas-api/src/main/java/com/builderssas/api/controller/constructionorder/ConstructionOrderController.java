package com.builderssas.api.controller.constructionorder;

import com.builderssas.api.domain.model.constructionorder.dto.ConstructionOrderDto;
import com.builderssas.api.mapper.construction.ConstructionOrderMapper;
import com.builderssas.api.repository.ConstructionOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/construction-orders")
@RequiredArgsConstructor
public class ConstructionOrderController {

    private final ConstructionOrderRepository orderRepo;
    private final ConstructionOrderMapper mapper;

    // ============================================================
    // GET ALL (CON RELACIONES)
    // ============================================================
    @GetMapping
    public List<ConstructionOrderDto> getAll() {
        return orderRepo.findAllWithRelations()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // ============================================================
    // GET BY ID (CON RELACIONES)
    // ============================================================
    @GetMapping("/{id}")
    public ResponseEntity<ConstructionOrderDto> getById(@PathVariable Long id) {
        return orderRepo.findByIdWithRelations(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ============================================================
    // GET BY PROJECT (CON RELACIONES)
    // ============================================================
    @GetMapping("/project/{projectId}")
    public List<ConstructionOrderDto> getByProject(@PathVariable Long projectId) {
        return orderRepo.findByProjectIdWithRelations(projectId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
