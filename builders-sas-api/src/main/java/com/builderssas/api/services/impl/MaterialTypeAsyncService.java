package com.builderssas.api.services.impl;

import com.builderssas.api.core.async.AbstractAsyncService;
import com.builderssas.api.domain.model.material.MaterialType;
import com.builderssas.api.domain.model.material.dto.MaterialTypeDto;
import com.builderssas.api.domain.model.material.dto.CreateMaterialTypeDto;
import com.builderssas.api.domain.model.material.dto.UpdateMaterialTypeDto;
import com.builderssas.api.mapper.material.MaterialTypeMapper;
import com.builderssas.api.repository.MaterialTypeRepository;
import com.builderssas.api.services.MaterialTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class MaterialTypeAsyncService
        extends AbstractAsyncService<
        MaterialType,
        MaterialTypeDto,
        CreateMaterialTypeDto,
        UpdateMaterialTypeDto>
        implements MaterialTypeService {

    private final MaterialTypeRepository repository;
    private final MaterialTypeMapper mapper;
    private final AsyncService asyncService;

    public MaterialTypeAsyncService(MaterialTypeRepository repository,
                                    MaterialTypeMapper mapper,
                                    AsyncService asyncService) {
        super(repository, mapper, asyncService);
        this.repository = repository;
        this.mapper = mapper;
        this.asyncService = asyncService;
    }

    @Override
    public CompletableFuture<MaterialTypeDto> create(CreateMaterialTypeDto dto) {
        return asyncService
                .supplyAsync(() -> mapper.toEntity(dto))
                .thenCompose(entity -> asyncService.supplyAsync(() -> repository.save(entity)))
                .thenApply(mapper::toDto);
    }

    @Override
    public CompletableFuture<MaterialTypeDto> getById(Long id) {
        return findByIdAsync(id);
    }

    @Override
    public CompletableFuture<List<MaterialTypeDto>> getAll() {
        return findAllAsync();
    }

    @Override
    public CompletableFuture<MaterialTypeDto> update(Long id, UpdateMaterialTypeDto dto) {
        return updateAsync(id, dto);
    }

    @Override
    public CompletableFuture<Void> delete(Long id) {
        return deleteAsync(id);
    }
}
