package com.builderssas.api.controller.materialtype;

import com.builderssas.api.domain.model.material.dto.CreateMaterialTypeDto;
import com.builderssas.api.domain.model.material.dto.MaterialTypeDto;
import com.builderssas.api.domain.model.material.dto.UpdateMaterialTypeDto;
import com.builderssas.api.services.MaterialTypeService;
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
 * ✅ REST Controller for managing MaterialType entities.
 * - Fully asynchronous and non-blocking
 * - Integrated with GlobalExceptionHandler
 * - Functional (no imperative try/catch)
 * - Declaratively validated with @Valid and @Validated
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/material-types")
@RequiredArgsConstructor
@Validated
public class MaterialTypeController {

    private final MaterialTypeService service;

    // ───────────────────────────────────────────────
    // CREATE
    // ───────────────────────────────────────────────
    @PostMapping
    public DeferredResult<ResponseEntity<MaterialTypeDto>> create(
            @Valid @RequestBody CreateMaterialTypeDto dto) {

        var result = new DeferredResult<ResponseEntity<MaterialTypeDto>>();
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
    // GET ALL
    // ───────────────────────────────────────────────
    @GetMapping
    public DeferredResult<ResponseEntity<List<MaterialTypeDto>>> getAll() {
        var result = new DeferredResult<ResponseEntity<List<MaterialTypeDto>>>();
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
    // GET BY ID
    // ───────────────────────────────────────────────
    @GetMapping("/{id}")
    public DeferredResult<ResponseEntity<MaterialTypeDto>> getById(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id) {

        var result = new DeferredResult<ResponseEntity<MaterialTypeDto>>();
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
    // UPDATE
    // ───────────────────────────────────────────────
    @PutMapping("/{id}")
    public DeferredResult<ResponseEntity<MaterialTypeDto>> update(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id,
            @Valid @RequestBody UpdateMaterialTypeDto dto) {

        var result = new DeferredResult<ResponseEntity<MaterialTypeDto>>();
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
    // DELETE
    // ───────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public DeferredResult<ResponseEntity<String>> delete(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id) {

        var result = new DeferredResult<ResponseEntity<String>>();
        service.delete(id)
                .thenApply(v -> ResponseEntity.ok("Material type deleted successfully"))
                .thenAccept(result::setResult)
                .exceptionally(ex -> {
                    result.setErrorResult(ex);
                    return null;
                });
        return result;
    }
}
