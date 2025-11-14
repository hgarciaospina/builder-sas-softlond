package com.builderssas.api.services;

import com.builderssas.api.domain.model.material.dto.CreateMaterialTypeDto;
import com.builderssas.api.domain.model.material.dto.MaterialTypeDto;
import com.builderssas.api.domain.model.material.dto.UpdateMaterialTypeDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for asynchronous MaterialType operations.
 */
public interface MaterialTypeService {

    CompletableFuture<MaterialTypeDto> create(CreateMaterialTypeDto dto);
    CompletableFuture<MaterialTypeDto> update(Long id, UpdateMaterialTypeDto dto);

    CompletableFuture<MaterialTypeDto> getById(Long id);

    CompletableFuture<List<MaterialTypeDto>> getAll();

    CompletableFuture<Void> delete(Long id);
}
