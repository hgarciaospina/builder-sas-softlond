package com.builderssas.api.mapper.construction;

import com.builderssas.api.domain.model.construction.ConstructionRequest;
import com.builderssas.api.domain.model.construction.request.dto.ConstructionRequestDto;
import com.builderssas.api.domain.model.construction.request.dto.CreateConstructionRequestDto;
import com.builderssas.api.domain.model.construction.ConstructionType;
import com.builderssas.api.domain.model.project.Project;
import com.builderssas.api.domain.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para transformar DTOs de solicitudes hacia/desde entidades.
 * MapStruct genera automáticamente la implementación.
 */
@Mapper(componentModel = "spring")
public interface ConstructionRequestMapper {

    /**
     * Convierte un DTO de creación en una entidad completa,
     * inyectando relaciones ya cargadas (Project, ConstructionType, User).
     *
     * Se ignora el ID porque se genera automáticamente en la BD.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "dto.latitude", target = "latitude")
    @Mapping(source = "dto.longitude", target = "longitude")
    @Mapping(source = "project", target = "project")
    @Mapping(source = "constructionType", target = "constructionType")
    @Mapping(source = "user", target = "requestedBy")
    @Mapping(target = "requestDate", ignore = true) // Se asigna en @PrePersist
    @Mapping(target = "requestStatus", ignore = true) // Lo define el servicio
    @Mapping(target = "observations", ignore = true)
    ConstructionRequest toEntity(
            CreateConstructionRequestDto dto,
            Project project,
            ConstructionType constructionType,
            User user
    );

    /**
     * Convierte una entidad a su DTO para el frontend.
     */
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "constructionType.id", target = "constructionTypeId")
    @Mapping(source = "requestedBy.id", target = "requestedByUserId")
    ConstructionRequestDto toDto(ConstructionRequest entity);
}
