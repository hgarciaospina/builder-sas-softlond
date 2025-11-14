package com.builderssas.api.services.impl;

import com.builderssas.api.core.async.AbstractAsyncService;
import com.builderssas.api.domain.model.project.Project;
import com.builderssas.api.domain.model.project.dto.ProjectDto;
import com.builderssas.api.domain.model.project.dto.CreateProjectDto;
import com.builderssas.api.domain.model.project.dto.UpdateProjectDto;
import com.builderssas.api.mapper.project.ProjectMapper;
import com.builderssas.api.repository.ProjectRepository;
import com.builderssas.api.services.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ProjectAsyncService
        extends AbstractAsyncService<
        Project,
        ProjectDto,
        CreateProjectDto,
        UpdateProjectDto>
        implements ProjectService {

    private final ProjectRepository repository;
    private final ProjectMapper mapper;
    private final AsyncService asyncService;

    public ProjectAsyncService(ProjectRepository repository,
                               ProjectMapper mapper,
                               AsyncService asyncService) {
        super(repository, mapper, asyncService);
        this.repository = repository;
        this.mapper = mapper;
        this.asyncService = asyncService;
    }

    @Override
    public CompletableFuture<ProjectDto> create(CreateProjectDto dto) {
        return asyncService
                .supplyAsync(() -> mapper.toEntity(dto))
                .thenCompose(entity -> asyncService.supplyAsync(() -> repository.save(entity)))
                .thenApply(mapper::toDto);
    }

    @Override
    public CompletableFuture<ProjectDto> getById(Long id) {
        return findByIdAsync(id);
    }

    @Override
    public CompletableFuture<List<ProjectDto>> getAll() {
        return findAllAsync();
    }

    @Override
    public CompletableFuture<ProjectDto> update(Long id, UpdateProjectDto dto) {
        return updateAsync(id, dto);
    }

    @Override
    public CompletableFuture<Void> delete(Long id) {
        return deleteAsync(id);
    }
}
