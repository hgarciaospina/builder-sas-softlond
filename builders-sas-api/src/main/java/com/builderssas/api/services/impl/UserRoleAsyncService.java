package com.builderssas.api.services.impl;

import com.builderssas.api.core.async.AbstractAsyncService;
import com.builderssas.api.domain.model.user.UserRole;
import com.builderssas.api.domain.model.user.dto.CreateUserRoleDto;
import com.builderssas.api.domain.model.user.dto.UpdateUserRoleDto;
import com.builderssas.api.domain.model.user.dto.UserRoleDto;
import com.builderssas.api.mapper.userrole.UserRoleMapper;
import com.builderssas.api.repository.RoleRepository;
import com.builderssas.api.repository.UserRepository;
import com.builderssas.api.repository.UserRoleRepository;
import com.builderssas.api.services.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class UserRoleAsyncService extends AbstractAsyncService<
        UserRole, UserRoleDto, CreateUserRoleDto, UpdateUserRoleDto>
        implements UserRoleService {

    private final UserRoleRepository repository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleMapper mapper;
    private final AsyncService asyncService;

    public UserRoleAsyncService(UserRoleRepository repository,
                                UserRepository userRepository,
                                RoleRepository roleRepository,
                                UserRoleMapper mapper,
                                AsyncService asyncService) {
        super(repository, mapper, asyncService);
        this.repository = repository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.mapper = mapper;
        this.asyncService = asyncService;
    }

    // =========================================================================
    // ✅ GET BY ID
    // =========================================================================
    @Override
    public CompletableFuture<UserRoleDto> getById(Long id) {
        return asyncService
                .supplyAsync(() ->
                        repository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("No existe UserRole con ID: " + id))
                )
                .thenApply(mapper::toDto);
    }

    // =========================================================================
    // ✅ GET ALL
    // =========================================================================
    @Override
    public CompletableFuture<List<UserRoleDto>> getAll() {
        return asyncService
                .supplyAsync(repository::findAll)
                .thenApply(mapper::toDtoList);
    }

    // =========================================================================
    // ✅ GET BY USER ID
    // =========================================================================
    @Override
    public CompletableFuture<List<UserRoleDto>> getByUserId(Long userId) {
        return asyncService
                .supplyAsync(() -> repository.findAll().stream()
                        .filter(ur -> ur.getUser().getId().equals(userId))
                        .toList()
                )
                .thenApply(mapper::toDtoList);
    }

    // =========================================================================
    // ✅ GET BY ROLE ID
    // =========================================================================
    @Override
    public CompletableFuture<List<UserRoleDto>> getByRoleId(Long roleId) {
        return asyncService
                .supplyAsync(() -> repository.findAll().stream()
                        .filter(ur -> ur.getRole().getId().equals(roleId))
                        .toList()
                )
                .thenApply(mapper::toDtoList);
    }

    // =========================================================================
    // ✅ CREATE (100% Funcional – sin IF)
    // =========================================================================
    @Override
    public CompletableFuture<UserRoleDto> create(CreateUserRoleDto dto) {

        return asyncService
                // Validar duplicado sin IF
                .supplyAsync(() -> repository.existsByUserIdAndRoleId(dto.getUserId(), dto.getRoleId()))
                .thenApply(exists ->
                        java.util.Optional.of(exists)
                                .filter(v -> !v)
                                .orElseThrow(() ->
                                        new IllegalArgumentException("El usuario ya tiene asignado ese rol.")
                                )
                )
                // Cargar usuario
                .thenCompose(v ->
                        asyncService.supplyAsync(() ->
                                userRepository.findById(dto.getUserId())
                                        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + dto.getUserId()))
                        )
                )
                // Combinar con rol
                .thenCombine(
                        asyncService.supplyAsync(() ->
                                roleRepository.findById(dto.getRoleId())
                                        .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + dto.getRoleId()))
                        ),
                        (user, role) ->
                                mapper.toEntity(dto)
                                        .withUser(user)
                                        .withRole(role)
                )
                // Guardar
                .thenCompose(entity ->
                        asyncService.supplyAsync(() -> repository.save(entity))
                )
                .thenApply(mapper::toDto);
    }

    // =========================================================================
    // ✅ UPDATE (100% Funcional – sin IF)
    // =========================================================================
    @Override
    public CompletableFuture<UserRoleDto> update(Long id, UpdateUserRoleDto dto) {

        return asyncService
                // Cargar existente
                .supplyAsync(() ->
                        repository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("No existe UserRole con ID: " + id))
                )
                // Mezclar campos escalares
                .thenApply(existing -> {
                    mapper.updateEntityFromDto(dto, existing);
                    return existing;
                })
                // Resolver nuevo User
                .thenCompose(existing ->
                        java.util.Optional.ofNullable(dto.getUserId())
                                .map(uid -> asyncService.supplyAsync(() ->
                                        userRepository.findById(uid)
                                                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + uid))
                                ).thenApply(user -> existing.withUser(user)))
                                .orElse(CompletableFuture.completedFuture(existing))
                )
                // Resolver nuevo Role
                .thenCompose(existing ->
                        java.util.Optional.ofNullable(dto.getRoleId())
                                .map(rid -> asyncService.supplyAsync(() ->
                                        roleRepository.findById(rid)
                                                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + rid))
                                ).thenApply(role -> existing.withRole(role)))
                                .orElse(CompletableFuture.completedFuture(existing))
                )
                // Guardar final
                .thenCompose(entity ->
                        asyncService.supplyAsync(() -> repository.save(entity))
                )
                .thenApply(mapper::toDto);
    }

    // =========================================================================
    // ✅ DELETE (100% Funcional – sin IF)
    // =========================================================================
    @Override
    public CompletableFuture<Boolean> delete(Long id) {

        return asyncService
                .supplyAsync(() ->
                        repository.existsById(id)
                )
                .thenApply(exists ->
                        java.util.Optional.of(exists)
                                .filter(v -> v)
                                .orElseThrow(() ->
                                        new IllegalArgumentException("No existe UserRole con ID: " + id)
                                )
                )
                .thenCompose(v ->
                        asyncService.runAsync(() -> repository.deleteById(id))
                )
                .thenApply(v -> true);
    }
}