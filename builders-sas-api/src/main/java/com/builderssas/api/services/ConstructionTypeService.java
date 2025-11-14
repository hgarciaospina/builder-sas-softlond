package com.builderssas.api.services;

import com.builderssas.api.domain.model.construction.dto.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for managing ConstructionType entities asynchronously.
 */
public interface ConstructionTypeService {

    CompletableFuture<ConstructionTypeDto> create(CreateConstructionTypeDto dto);

    CompletableFuture<List<ConstructionTypeDto>> getAll();

    CompletableFuture<ConstructionTypeDto> getById(Long id);

    CompletableFuture<ConstructionTypeDto> update(Long id, UpdateConstructionTypeDto dto);

    CompletableFuture<Void> delete(Long id);
}
