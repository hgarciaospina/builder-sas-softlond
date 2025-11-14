package com.builderssas.api.controller.role;

import com.builderssas.api.domain.model.user.dto.CreateRoleDto;
import com.builderssas.api.domain.model.user.dto.RoleDto;
import com.builderssas.api.domain.model.user.dto.UpdateRoleDto;
import com.builderssas.api.services.RoleService;
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
 * ✅ REST Controller for managing roles asynchronously.
 * - Fully non-blocking
 * - Integrated with GlobalExceptionHandler
 * - Uses DeferredResult to propagate async exceptions
 * - Declaratively validated with @Valid and @Validated
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Validated
public class RoleController {

    private final RoleService service;

    // ───────────────────────────────────────────────
    // CREATE
    // ───────────────────────────────────────────────
    @PostMapping
    public DeferredResult<ResponseEntity<RoleDto>> create(
            @Valid @RequestBody CreateRoleDto dto) {

        var result = new DeferredResult<ResponseEntity<RoleDto>>();
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
    public DeferredResult<ResponseEntity<List<RoleDto>>> getAll() {
        var result = new DeferredResult<ResponseEntity<List<RoleDto>>>();
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
    public DeferredResult<ResponseEntity<RoleDto>> getById(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id) {

        var result = new DeferredResult<ResponseEntity<RoleDto>>();
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
    public DeferredResult<ResponseEntity<RoleDto>> update(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id,
            @Valid @RequestBody UpdateRoleDto dto) {

        var result = new DeferredResult<ResponseEntity<RoleDto>>();
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
                .thenApply(v -> ResponseEntity.ok("Role deleted successfully"))
                .thenAccept(result::setResult)
                .exceptionally(ex -> {
                    result.setErrorResult(ex);
                    return null;
                });
        return result;
    }
}
