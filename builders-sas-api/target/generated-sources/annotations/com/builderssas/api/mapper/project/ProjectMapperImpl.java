package com.builderssas.api.mapper.project;

import com.builderssas.api.domain.model.project.Project;
import com.builderssas.api.domain.model.project.dto.CreateProjectDto;
import com.builderssas.api.domain.model.project.dto.ProjectDto;
import com.builderssas.api.domain.model.project.dto.UpdateProjectDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class ProjectMapperImpl implements ProjectMapper {

    @Override
    public ProjectDto toDto(Project entity) {
        if ( entity == null ) {
            return null;
        }

        ProjectDto.ProjectDtoBuilder projectDto = ProjectDto.builder();

        projectDto.id( entity.getId() );
        projectDto.name( entity.getName() );
        projectDto.description( entity.getDescription() );
        projectDto.projectStartDate( entity.getProjectStartDate() );
        projectDto.projectEndDate( entity.getProjectEndDate() );
        projectDto.progressPercentage( entity.getProgressPercentage() );
        if ( entity.getProjectStatus() != null ) {
            projectDto.projectStatus( entity.getProjectStatus().name() );
        }

        return projectDto.build();
    }

    @Override
    public Project toEntity(CreateProjectDto dto) {
        if ( dto == null ) {
            return null;
        }

        Project.ProjectBuilder project = Project.builder();

        project.name( dto.getName() );
        project.description( dto.getDescription() );

        return project.build();
    }

    @Override
    public void updateEntityFromDto(UpdateProjectDto dto, Project entity) {
        if ( dto == null ) {
            return;
        }

        entity.setName( dto.getName() );
        entity.setDescription( dto.getDescription() );
    }

    @Override
    public List<ProjectDto> toDtoList(List<Project> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ProjectDto> list = new ArrayList<ProjectDto>( entities.size() );
        for ( Project project : entities ) {
            list.add( toDto( project ) );
        }

        return list;
    }
}
