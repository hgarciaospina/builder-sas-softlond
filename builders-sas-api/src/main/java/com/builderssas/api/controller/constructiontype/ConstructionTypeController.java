package com.builderssas.api.controller.constructiontype;

import com.builderssas.api.domain.model.construction.dto.*;
import com.builderssas.api.services.ConstructionTypeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

/**
 * 🏗️ ConstructionTypeController
 *
 * Controlador REST completamente asíncrono y validado.
 * Maneja los tipos de construcción (ConstructionType) mediante programación no bloqueante.
 * Integra validación declarativa con @Valid / @Validated y propaga errores al GlobalExceptionHandler.
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/construction-types")
@RequiredArgsConstructor
@Validated
public class ConstructionTypeController {

    private final ConstructionTypeService service;

    // ───────────────────────────────────────────────
    // GET ALL
    // ───────────────────────────────────────────────
    @GetMapping
    public DeferredResult<ResponseEntity<List<ConstructionTypeDto>>> getAll() {
        var result = new DeferredResult<ResponseEntity<List<ConstructionTypeDto>>>();
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
    public DeferredResult<ResponseEntity<ConstructionTypeDto>> getById(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id) {

        var result = new DeferredResult<ResponseEntity<ConstructionTypeDto>>();
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
    // CREATE
    // ───────────────────────────────────────────────
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeferredResult<ConstructionTypeDto> create(
            @Valid @RequestBody CreateConstructionTypeDto dto) {

        var result = new DeferredResult<ConstructionTypeDto>();
        service.create(dto)
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
    public DeferredResult<ResponseEntity<ConstructionTypeDto>> update(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id,
            @Valid @RequestBody UpdateConstructionTypeDto dto) {

        var result = new DeferredResult<ResponseEntity<ConstructionTypeDto>>();
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public DeferredResult<Void> delete(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id) {

        var result = new DeferredResult<Void>();
        service.delete(id)
                .thenAccept(result::setResult)
                .exceptionally(ex -> {
                    result.setErrorResult(ex);
                    return null;
                });
        return result;
    }
}
