package com.builderssas.api.controller.userrole;

import com.builderssas.api.domain.model.user.dto.CreateUserRoleDto;
import com.builderssas.api.domain.model.user.dto.UpdateUserRoleDto;
import com.builderssas.api.domain.model.user.dto.UserRoleDto;
import com.builderssas.api.services.UserRoleService;
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
 * ✅ REST Controller for UserRole management.
 * - Fully asynchronous (DeferredResult + CompletableFuture)
 * - Delegates all logic to the service layer
 * - No blocking calls or imperative code
 * - Fully validated and ready for production
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/user-roles")
@RequiredArgsConstructor
@Validated
public class UserRoleController {

    private final UserRoleService service;

    // ───────────────────────────────────────────────
    // ✅ CREATE
    // ───────────────────────────────────────────────
    @PostMapping
    public DeferredResult<ResponseEntity<UserRoleDto>> create(
            @Valid @RequestBody CreateUserRoleDto dto) {

        var result = new DeferredResult<ResponseEntity<UserRoleDto>>();

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
    // ✅ GET ALL
    // ───────────────────────────────────────────────
    @GetMapping
    public DeferredResult<ResponseEntity<List<UserRoleDto>>> getAll() {
        var result = new DeferredResult<ResponseEntity<List<UserRoleDto>>>();

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
    // ✅ GET BY ID
    // ───────────────────────────────────────────────
    @GetMapping("/{id}")
    public DeferredResult<ResponseEntity<UserRoleDto>> getById(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.")
            Long id) {

        var result = new DeferredResult<ResponseEntity<UserRoleDto>>();

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
    // ✅ UPDATE
    // ───────────────────────────────────────────────
    @PutMapping("/{id}")
    public DeferredResult<ResponseEntity<UserRoleDto>> update(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.")
            Long id,
            @Valid @RequestBody UpdateUserRoleDto dto) {

        var result = new DeferredResult<ResponseEntity<UserRoleDto>>();

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
    // ✅ DELETE
    // ───────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public DeferredResult<ResponseEntity<String>> delete(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.")
            Long id) {

        var result = new DeferredResult<ResponseEntity<String>>();

        service.delete(id)
                .thenApply(v -> ResponseEntity.ok("User role deleted successfully"))
                .thenAccept(result::setResult)
                .exceptionally(ex -> {
                    result.setErrorResult(ex);
                    return null;
                });

        return result;
    }
}
