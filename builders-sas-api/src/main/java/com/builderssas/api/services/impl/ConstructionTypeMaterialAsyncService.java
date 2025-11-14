package com.builderssas.api.services.impl;

import com.builderssas.api.core.async.AbstractAsyncService;
import com.builderssas.api.domain.model.construction.ConstructionTypeMaterial;
import com.builderssas.api.domain.model.construction.dto.ConstructionTypeMaterialDto;
import com.builderssas.api.domain.model.construction.dto.CreateConstructionTypeMaterialDto;
import com.builderssas.api.domain.model.construction.dto.UpdateConstructionTypeMaterialDto;
import com.builderssas.api.mapper.construction.ConstructionTypeMaterialMapper;
import com.builderssas.api.repository.ConstructionTypeMaterialRepository;
import com.builderssas.api.services.ConstructionTypeMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ConstructionTypeMaterialAsyncService extends AbstractAsyncService<
        ConstructionTypeMaterial,
        ConstructionTypeMaterialDto,
        CreateConstructionTypeMaterialDto,
        UpdateConstructionTypeMaterialDto>
        implements ConstructionTypeMaterialService {

    private final ConstructionTypeMaterialRepository repository;
    private final ConstructionTypeMaterialMapper mapper;
    private final AsyncService asyncService;

    public ConstructionTypeMaterialAsyncService(
            ConstructionTypeMaterialRepository repository,
            ConstructionTypeMaterialMapper mapper,
            AsyncService asyncService
    ) {
        super(repository, mapper, asyncService);
        this.repository = repository;
        this.mapper = mapper;
        this.asyncService = asyncService;
    }

    // =========================================
    // ✅ FIX: SOBREESCRIBIR findAllAsync()
    // =========================================
    @Override
    public CompletableFuture<List<ConstructionTypeMaterialDto>> getAll() {
        return asyncService
                .supplyAsync(repository::findAllWithRelations)
                .thenApply(mapper::toDtoList);
    }

    // =========================================
    // ✅ FIX: SOBREESCRIBIR findByIdAsync()
    // =========================================
    @Override
    public CompletableFuture<ConstructionTypeMaterialDto> getById(Long id) {
        return asyncService
                .supplyAsync(() -> repository.findByIdWithRelations(id)
                        .orElseThrow(() -> new RuntimeException("Registro no encontrado: " + id)))
                .thenApply(mapper::toDto);
    }

    // =========================================
    // ✅ CREATE
    // =========================================
    @Override
    public CompletableFuture<ConstructionTypeMaterialDto> create(CreateConstructionTypeMaterialDto dto) {
        return asyncService
                .supplyAsync(() -> mapper.toEntity(dto))
                .thenCompose(entity -> asyncService.supplyAsync(() -> repository.save(entity)))
                .thenApply(mapper::toDto);
    }

    // =========================================
    // ✅ UPDATE
    // =========================================
    @Override
    public CompletableFuture<ConstructionTypeMaterialDto> update(Long id, UpdateConstructionTypeMaterialDto dto) {
        return updateAsync(id, dto);
    }

    // =========================================
    // ✅ DELETE
    // =========================================
    @Override
    public CompletableFuture<Void> delete(Long id) {
        return deleteAsync(id);
    }
}
