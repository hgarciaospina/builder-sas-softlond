package com.builderssas.api.core.async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Generic asynchronous CRUD contract for all services.
 * Eliminates imperative patterns and ensures non-blocking data access.
 */
public interface AsyncCrudService<DTO, CreateDTO, UpdateDTO> {

    CompletableFuture<DTO> getById(Long id);

    CompletableFuture<List<DTO>> getAll();

    CompletableFuture<DTO> create(CreateDTO dto);

    CompletableFuture<DTO> update(Long id, UpdateDTO dto);

    CompletableFuture<Boolean> delete(Long id);
}
