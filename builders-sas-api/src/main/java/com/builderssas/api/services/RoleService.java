package com.builderssas.api.services;

import com.builderssas.api.domain.model.user.dto.CreateRoleDto;
import com.builderssas.api.domain.model.user.dto.RoleDto;
import com.builderssas.api.domain.model.user.dto.UpdateRoleDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Contract for asynchronous CRUD operations on Role entities.
 */
public interface RoleService {

    CompletableFuture<RoleDto> create(CreateRoleDto dto);

    CompletableFuture<RoleDto> getById(Long id);

    CompletableFuture<List<RoleDto>> getAll();

    CompletableFuture<RoleDto> update(Long id, UpdateRoleDto dto);

    CompletableFuture<Void> delete(Long id); // ✅ tipo correcto
}
