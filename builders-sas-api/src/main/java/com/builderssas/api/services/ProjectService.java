package com.builderssas.api.services;

import com.builderssas.api.domain.model.project.dto.CreateProjectDto;
import com.builderssas.api.domain.model.project.dto.UpdateProjectDto;
import com.builderssas.api.domain.model.project.dto.ProjectDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * ✅ Interfaz de servicio para la gestión asíncrona de proyectos.
 * Todas las operaciones son no bloqueantes (basadas en CompletableFuture).
 */
public interface ProjectService {

    /**
     * Crea un nuevo proyecto de forma asíncrona.
     */
    CompletableFuture<ProjectDto> create(CreateProjectDto dto);

    /**
     * Obtiene un proyecto por su ID.
     */
    CompletableFuture<ProjectDto> getById(Long id);

    /**
     * Obtiene todos los proyectos.
     */
    CompletableFuture<List<ProjectDto>> getAll();

    /**
     * Actualiza un proyecto existente.
     */
    CompletableFuture<ProjectDto> update(Long id, UpdateProjectDto dto);

    /**
     * Elimina un proyecto por ID.
     */
    CompletableFuture<Void> delete(Long id);
}
