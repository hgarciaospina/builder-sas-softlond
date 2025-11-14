package com.builderssas.api.services.impl;

import com.builderssas.api.core.async.AbstractAsyncService;
import com.builderssas.api.domain.model.construction.ConstructionType;
import com.builderssas.api.domain.model.construction.dto.ConstructionTypeDto;
import com.builderssas.api.domain.model.construction.dto.CreateConstructionTypeDto;
import com.builderssas.api.domain.model.construction.dto.UpdateConstructionTypeDto;
import com.builderssas.api.mapper.ctype.ConstructionTypeMapper;
import com.builderssas.api.repository.ConstructionTypeRepository;
import com.builderssas.api.services.ConstructionTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ConstructionTypeAsyncService
        extends AbstractAsyncService<
        ConstructionType,
        ConstructionTypeDto,
        CreateConstructionTypeDto,
        UpdateConstructionTypeDto>
        implements ConstructionTypeService {

    private final ConstructionTypeRepository repository;
    private final ConstructionTypeMapper mapper;
    private final AsyncService asyncService;

    public ConstructionTypeAsyncService(ConstructionTypeRepository repository,
                                        ConstructionTypeMapper mapper,
                                        AsyncService asyncService) {
        super(repository, mapper, asyncService);
        this.repository = repository;
        this.mapper = mapper;
        this.asyncService = asyncService;
    }

    @Override
    public CompletableFuture<ConstructionTypeDto> create(CreateConstructionTypeDto dto) {
        return asyncService
                .supplyAsync(() -> mapper.toEntity(dto))
                .thenCompose(entity -> asyncService.supplyAsync(() -> repository.save(entity)))
                .thenApply(mapper::toDto);
    }

    @Override
    public CompletableFuture<ConstructionTypeDto> getById(Long id) {
        return findByIdAsync(id);
    }

    @Override
    public CompletableFuture<List<ConstructionTypeDto>> getAll() {
        return findAllAsync();
    }

    @Override
    public CompletableFuture<ConstructionTypeDto> update(Long id, UpdateConstructionTypeDto dto) {
        return updateAsync(id, dto);
    }

    @Override
    public CompletableFuture<Void> delete(Long id) {
        return deleteAsync(id);
    }
}
