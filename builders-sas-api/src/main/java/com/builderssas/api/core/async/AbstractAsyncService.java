package com.builderssas.api.core.async;

import com.builderssas.api.services.impl.AsyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 🧩 Base asynchronous CRUD service.
 * Provides reusable, thread-safe async operations for all entities.
 *
 * @param <E> Entity type
 * @param <DTO> DTO type
 * @param <CreateDTO> DTO used for creation
 * @param <UpdateDTO> DTO used for updates
 */
@RequiredArgsConstructor
public abstract class AbstractAsyncService<
        E,
        DTO,
        CreateDTO,
        UpdateDTO> {

    protected final JpaRepository<E, Long> repository;
    protected final BaseMapper<E, DTO, CreateDTO, UpdateDTO> mapper;
    protected final AsyncService asyncService;

    /**
     * Saves an entity asynchronously and returns its DTO.
     */
    public CompletableFuture<DTO> saveAsync(E entity) {
        return asyncService.supplyAsync(() -> repository.save(entity))
                .thenApply(mapper::toDto);
    }

    /**
     * Retrieves all entities asynchronously.
     */
    public CompletableFuture<List<DTO>> findAllAsync() {
        return asyncService.supplyAsync(repository::findAll)
                .thenApply(mapper::toDtoList);
    }

    /**
     * Retrieves one entity asynchronously by ID.
     */
    public CompletableFuture<DTO> findByIdAsync(Long id) {
        return asyncService.supplyAsync(() ->
                        repository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Entity not found")))
                .thenApply(mapper::toDto);
    }

    /**
     * Deletes an entity asynchronously by ID.
     */
    public CompletableFuture<Void> deleteAsync(Long id) {
        return asyncService.runAsync(() -> {
            if (!repository.existsById(id)) {
                throw new RuntimeException("Entity not found");
            }
            repository.deleteById(id);
        });
    }

    /**
     * Updates an entity asynchronously.
     */
    public CompletableFuture<DTO> updateAsync(Long id, UpdateDTO dto) {
        return asyncService.supplyAsync(() -> {
                    E entity = repository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Entity not found"));
                    mapper.updateEntityFromDto(dto, entity);
                    return repository.save(entity);
                })
                .thenApply(mapper::toDto);
    }
}
