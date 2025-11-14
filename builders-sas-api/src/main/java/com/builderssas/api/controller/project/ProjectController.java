package com.builderssas.api.controller.project;

import com.builderssas.api.domain.model.project.dto.CreateProjectDto;
import com.builderssas.api.domain.model.project.dto.ProjectDto;
import com.builderssas.api.domain.model.project.dto.UpdateProjectDto;
import com.builderssas.api.services.ProjectService;
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
 * ✅ REST Controller para la gestión de proyectos.
 * - Totalmente asíncrono (usa DeferredResult + CompletableFuture)
 * - Integrado con GlobalExceptionHandler
 * - Validado con @Valid y @Validated
 * - Sin lógica imperativa
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {

    private final ProjectService projectService;

    // ───────────────────────────────────────────────
    // CREATE
    // ───────────────────────────────────────────────
    @PostMapping
    public DeferredResult<ResponseEntity<ProjectDto>> createProject(
            @Valid @RequestBody CreateProjectDto dto) {

        var result = new DeferredResult<ResponseEntity<ProjectDto>>();
        projectService.create(dto)
                .thenApply(project -> ResponseEntity.status(201).body(project))
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
    public DeferredResult<ResponseEntity<ProjectDto>> getById(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id) {

        var result = new DeferredResult<ResponseEntity<ProjectDto>>();
        projectService.getById(id)
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
    public DeferredResult<ResponseEntity<List<ProjectDto>>> getAll() {
        var result = new DeferredResult<ResponseEntity<List<ProjectDto>>>();
        projectService.getAll()
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
    public DeferredResult<ResponseEntity<ProjectDto>> updateProject(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id,
            @Valid @RequestBody UpdateProjectDto dto) {

        var result = new DeferredResult<ResponseEntity<ProjectDto>>();
        projectService.update(id, dto)
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
    public DeferredResult<ResponseEntity<String>> deleteProject(
            @PathVariable
            @NotNull(message = "El ID no puede ser nulo.")
            @Min(value = 1, message = "El ID debe ser mayor que cero.") Long id) {

        var result = new DeferredResult<ResponseEntity<String>>();
        projectService.delete(id)
                .thenApply(v -> ResponseEntity.ok("Proyecto eliminado correctamente"))
                .thenAccept(result::setResult)
                .exceptionally(ex -> {
                    result.setErrorResult(ex);
                    return null;
                });
        return result;
    }
}
