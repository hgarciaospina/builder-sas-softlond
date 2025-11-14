package com.builderssas.api.controller.user;

import com.builderssas.api.domain.model.user.dto.*;
import com.builderssas.api.services.UserService;

import com.builderssas.api.services.impl.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final AuthService authService;   //Se agrega la inyección del servicio de login

    // GET BY ID
    @GetMapping("/{id}")
    public DeferredResult<ResponseEntity<UserDto>> getById(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id) {

        var result = new DeferredResult<ResponseEntity<UserDto>>();

        userService.getById(id)
                .thenApply(ResponseEntity::ok)
                .thenAccept(result::setResult)
                .exceptionally(ex -> { result.setErrorResult(ex); return null; });

        return result;
    }

    // GET ALL
    @GetMapping
    public DeferredResult<ResponseEntity<List<UserDto>>> getAll() {

        var result = new DeferredResult<ResponseEntity<List<UserDto>>>();

        userService.getAll()
                .thenApply(ResponseEntity::ok)
                .thenAccept(result::setResult)
                .exceptionally(ex -> { result.setErrorResult(ex); return null; });

        return result;
    }

    // CREATE
    @PostMapping
    public DeferredResult<ResponseEntity<UserDto>> create(@Valid @RequestBody CreateUserDto dto) {

        var result = new DeferredResult<ResponseEntity<UserDto>>();

        userService.create(dto)
                .thenApply(ResponseEntity::ok)
                .thenAccept(result::setResult)
                .exceptionally(ex -> { result.setErrorResult(ex); return null; });

        return result;
    }

    // UPDATE
    @PutMapping("/{id}")
    public DeferredResult<ResponseEntity<UserDto>> update(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id,
            @Valid @RequestBody UpdateUserDto dto) {

        var result = new DeferredResult<ResponseEntity<UserDto>>();

        userService.update(id, dto)
                .thenApply(ResponseEntity::ok)
                .thenAccept(result::setResult)
                .exceptionally(ex -> { result.setErrorResult(ex); return null; });

        return result;
    }

    // ───────────────────────────────────────────────
    // LOGIN 100% ASYNC + Devuelve usuario + roles
    // ───────────────────────────────────────────────
    @PostMapping("/login")
    public DeferredResult<ResponseEntity<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto dto) {

        var result = new DeferredResult<ResponseEntity<LoginResponseDto>>();

        authService.login(dto.getUsername(), dto.getPassword())
                .thenApply(ResponseEntity::ok)
                .thenAccept(result::setResult)
                .exceptionally(ex -> {
                    result.setErrorResult(ex);
                    return null;
                });

        return result;
    }

    // DELETE
    @DeleteMapping("/{id}")
    public DeferredResult<ResponseEntity<String>> delete(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id) {

        var result = new DeferredResult<ResponseEntity<String>>();

        userService.delete(id)
                .thenApply(v -> ResponseEntity.ok("User deleted successfully"))
                .thenAccept(result::setResult)
                .exceptionally(ex -> { result.setErrorResult(ex); return null; });

        return result;
    }
}
