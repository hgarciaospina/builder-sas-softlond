package com.builderssas.api.services.impl;

import com.builderssas.api.core.async.AbstractAsyncService;
import com.builderssas.api.domain.model.user.Role;
import com.builderssas.api.domain.model.user.dto.RoleDto;
import com.builderssas.api.domain.model.user.dto.CreateRoleDto;
import com.builderssas.api.domain.model.user.dto.UpdateRoleDto;
import com.builderssas.api.mapper.role.RoleMapper;
import com.builderssas.api.repository.RoleRepository;
import com.builderssas.api.services.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class RoleAsyncService
        extends AbstractAsyncService<Role, RoleDto, CreateRoleDto, UpdateRoleDto>
        implements RoleService {

    private final RoleRepository repository;
    private final RoleMapper mapper;
    private final AsyncService asyncService;

    public RoleAsyncService(RoleRepository repository,
                            RoleMapper mapper,
                            AsyncService asyncService) {
        super(repository, mapper, asyncService);
        this.repository = repository;
        this.mapper = mapper;
        this.asyncService = asyncService;
    }

    @Override
    public CompletableFuture<RoleDto> create(CreateRoleDto dto) {
        return asyncService
                .supplyAsync(() -> mapper.toEntity(dto))
                .thenCompose(entity -> asyncService.supplyAsync(() -> repository.save(entity)))
                .thenApply(mapper::toDto);
    }

    @Override
    public CompletableFuture<RoleDto> getById(Long id) {
        return findByIdAsync(id);
    }

    @Override
    public CompletableFuture<List<RoleDto>> getAll() {
        return findAllAsync();
    }

    @Override
    public CompletableFuture<RoleDto> update(Long id, UpdateRoleDto dto) {
        return updateAsync(id, dto);
    }

    @Override
    public CompletableFuture<Void> delete(Long id) {
        return deleteAsync(id);
    }
}
