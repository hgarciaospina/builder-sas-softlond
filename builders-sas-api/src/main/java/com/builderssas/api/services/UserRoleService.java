package com.builderssas.api.services;

import com.builderssas.api.domain.model.user.dto.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Asynchronous service interface for managing UserRole relationships.
 */
public interface UserRoleService {

    CompletableFuture<UserRoleDto> getById(Long id);

    CompletableFuture<List<UserRoleDto>> getAll();

    CompletableFuture<List<UserRoleDto>> getByUserId(Long userId);

    CompletableFuture<List<UserRoleDto>> getByRoleId(Long roleId);

    CompletableFuture<UserRoleDto> create(CreateUserRoleDto dto);

    /**
     * ✅ Added to support update operation used by the controller.
     */
    CompletableFuture<UserRoleDto> update(Long id, UpdateUserRoleDto dto);

    CompletableFuture<Boolean> delete(Long id);
}
