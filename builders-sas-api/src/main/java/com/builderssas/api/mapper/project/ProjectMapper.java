package com.builderssas.api.mapper.project;

import com.builderssas.api.core.async.BaseMapper;
import com.builderssas.api.domain.model.project.Project;
import com.builderssas.api.domain.model.project.dto.ProjectDto;
import com.builderssas.api.domain.model.project.dto.CreateProjectDto;
import com.builderssas.api.domain.model.project.dto.UpdateProjectDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper extends
        BaseMapper<Project, ProjectDto, CreateProjectDto, UpdateProjectDto> {

    @Override ProjectDto toDto(Project entity);
    @Override Project toEntity(CreateProjectDto dto);
    @Override void updateEntityFromDto(UpdateProjectDto dto, @MappingTarget Project entity);
    @Override List<ProjectDto> toDtoList(List<Project> entities);
}
