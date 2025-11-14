package com.builderssas.api.services.impl;

import com.builderssas.api.core.async.AbstractAsyncService;
import com.builderssas.api.domain.model.user.User;
import com.builderssas.api.domain.model.user.dto.*;
import com.builderssas.api.mapper.user.UserMapper;
import com.builderssas.api.repository.UserRepository;
import com.builderssas.api.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class UserAsyncService
        extends AbstractAsyncService<User, UserDto, CreateUserDto, UpdateUserDto>
        implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final AsyncService asyncService;

    public UserAsyncService(
            UserRepository repository,
            UserMapper mapper,
            AsyncService asyncService
    ) {
        super(repository, mapper, asyncService);
        this.repository = repository;
        this.mapper = mapper;
        this.asyncService = asyncService;
    }

    // ───────────────────────────────────────────────
    // ✅ CREATE
    // ───────────────────────────────────────────────
    @Override
    public CompletableFuture<UserDto> create(CreateUserDto dto) {
        return asyncService.supplyAsync(() -> mapper.toEntity(dto))
                .thenCompose(entity -> asyncService.supplyAsync(() -> repository.save(entity)))
                .thenApply(mapper::toDto);
    }

    // ───────────────────────────────────────────────
    // ✅ GET BY ID
    // ───────────────────────────────────────────────
    @Override
    public CompletableFuture<UserDto> getById(Long id) {
        return findByIdAsync(id);
    }

    // ───────────────────────────────────────────────
    // ✅ GET ALL
    // ───────────────────────────────────────────────
    @Override
    public CompletableFuture<List<UserDto>> getAll() {
        return findAllAsync();
    }

    // ───────────────────────────────────────────────
    // ✅ UPDATE
    // ───────────────────────────────────────────────
    @Override
    public CompletableFuture<UserDto> update(Long id, UpdateUserDto dto) {
        return updateAsync(id, dto);
    }

    // ───────────────────────────────────────────────
    // ✅ DELETE
    // ───────────────────────────────────────────────
    @Override
    public CompletableFuture<Void> delete(Long id) {
        return deleteAsync(id);
    }

    // ───────────────────────────────────────────────
    // ✅ LOGIN — FUERA DE LA HERENCIA DEL CRUD
    // ───────────────────────────────────────────────
    @Override
    public CompletableFuture<LoginResponseDto> login(String username, String rawPassword) {

        return asyncService.supplyAsync(() ->
                        repository.findByUsernameWithRoles(username)
                                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"))
                )
                .thenApply(user -> {
                    if (!user.getPassword().equals(rawPassword)) {
                        throw new IllegalArgumentException("Credenciales inválidas");
                    }

                    return LoginResponseDto.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .firstname(user.getFirstname())
                            .lastname(user.getLastname())
                            .email(user.getEmail())
                            .roles(
                                    user.getRoles()
                                            .stream()
                                            .map(r -> r.getRole().getName())
                                            .toList()
                            )
                            .build();
                });
    }

}
