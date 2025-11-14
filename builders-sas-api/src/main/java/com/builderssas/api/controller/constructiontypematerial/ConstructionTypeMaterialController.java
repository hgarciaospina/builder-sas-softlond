package com.builderssas.api.controller.constructiontypematerial;

import com.builderssas.api.domain.model.construction.dto.*;
import com.builderssas.api.services.ConstructionTypeMaterialService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

/**
 * ✅ REST Controller for ConstructionTypeMaterial CRUD.
 * - Fully asynchronous (non-blocking)
 * - Uses CompletableFuture + DeferredResult for concurrency
 * - Functional and validated (no imperative error handling)
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/construction-type-materials")
@RequiredArgsConstructor
@Validated
public class ConstructionTypeMaterialController {

    private final ConstructionTypeMaterialService service;

    // ───────────────────────────────────────────────
    // 🔹 CREATE
    // ───────────────────────────────────────────────
    @PostMapping
    public DeferredResult<ResponseEntity<ConstructionTypeMaterialDto>> create(
            @Valid @RequestBody CreateConstructionTypeMaterialDto dto) {

        var result = new DeferredResult<ResponseEntity<ConstructionTypeMaterialDto>>();
        service.create(dto)
                .thenApply(ResponseEntity::ok)
                .thenAccept(result::setResult)
                .exceptionally(ex -> {
                    result.setErrorResult(ex);
                    return null;
                });
        return result;
    }

    // ───────────────────────────────────────────────
    // 🔹 GET ALL
    // ───────────────────────────────────────────────
    @GetMapping
    public DeferredResult<ResponseEntity<List<ConstructionTypeMaterialDto>>> getAll() {
        var result = new DeferredResult<ResponseEntity<List<ConstructionTypeMaterialDto>>>();
        service.getAll()
                .thenApply(ResponseEntity::ok)
                .thenAccept(result::setResult)
                .exceptionally(ex -> {
                    result.setErrorResult(ex);
                    return null;
                });
        return result;
    }

    // ───────────────────────────────────────────────
    // 🔹 GET BY ID
    // ───────────────────────────────────────────────
    @GetMapping("/{id}")
    public DeferredResult<ResponseEntity<ConstructionTypeMaterialDto>> getById(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id) {

        var result = new DeferredResult<ResponseEntity<ConstructionTypeMaterialDto>>();
        service.getById(id)
                .thenApply(ResponseEntity::ok)
                .thenAccept(result::setResult)
                .exceptionally(ex -> {
                    result.setErrorResult(ex);
                    return null;
                });
        return result;
    }

    // ───────────────────────────────────────────────
    // 🔹 UPDATE
    // ───────────────────────────────────────────────
    @PutMapping("/{id}")
    public DeferredResult<ResponseEntity<ConstructionTypeMaterialDto>> update(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id,
            @Valid @RequestBody UpdateConstructionTypeMaterialDto dto) {

        var result = new DeferredResult<ResponseEntity<ConstructionTypeMaterialDto>>();
        service.update(id, dto)
                .thenApply(ResponseEntity::ok)
                .thenAccept(result::setResult)
                .exceptionally(ex -> {
                    result.setErrorResult(ex);
                    return null;
                });
        return result;
    }

    // ───────────────────────────────────────────────
    // 🔹 DELETE
    // ───────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public DeferredResult<ResponseEntity<String>> delete(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id) {

        var result = new DeferredResult<ResponseEntity<String>>();
        service.delete(id)
                .thenApply(v -> ResponseEntity.ok("Record deleted successfully"))
                .thenAccept(result::setResult)
                .exceptionally(ex -> {
                    result.setErrorResult(ex);
                    return null;
                });
        return result;
    }
}
