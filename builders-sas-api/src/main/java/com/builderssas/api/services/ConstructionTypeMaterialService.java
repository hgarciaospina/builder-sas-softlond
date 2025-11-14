package com.builderssas.api.services;

import com.builderssas.api.domain.model.construction.dto.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * ✅ Service interface for ConstructionTypeMaterial CRUD.
 * All operations are asynchronous and functional.
 */
public interface ConstructionTypeMaterialService {

    CompletableFuture<ConstructionTypeMaterialDto> create(CreateConstructionTypeMaterialDto dto);
    CompletableFuture<ConstructionTypeMaterialDto> getById(Long id);
    CompletableFuture<List<ConstructionTypeMaterialDto>> getAll();
    CompletableFuture<ConstructionTypeMaterialDto> update(Long id, UpdateConstructionTypeMaterialDto dto);
    CompletableFuture<Void> delete(Long id);
}
