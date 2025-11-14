package com.builderssas.api.mapper.construction;

import com.builderssas.api.domain.model.construction.ConstructionRequest;
import com.builderssas.api.domain.model.construction.ConstructionType;
import com.builderssas.api.domain.model.construction.request.dto.ConstructionRequestDto;
import com.builderssas.api.domain.model.construction.request.dto.CreateConstructionRequestDto;
import com.builderssas.api.domain.model.project.Project;
import com.builderssas.api.domain.model.user.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Microsoft)"
)
@Component
public class ConstructionRequestMapperImpl implements ConstructionRequestMapper {

    @Override
    public ConstructionRequest toEntity(CreateConstructionRequestDto dto, Project project, ConstructionType constructionType, User user) {
        if ( dto == null && project == null && constructionType == null && user == null ) {
            return null;
        }

        ConstructionRequest.ConstructionRequestBuilder constructionRequest = ConstructionRequest.builder();

        if ( dto != null ) {
            constructionRequest.latitude( dto.getLatitude() );
            constructionRequest.longitude( dto.getLongitude() );
        }
        constructionRequest.project( project );
        constructionRequest.constructionType( constructionType );
        constructionRequest.requestedBy( user );

        return constructionRequest.build();
    }

    @Override
    public ConstructionRequestDto toDto(ConstructionRequest entity) {
        if ( entity == null ) {
            return null;
        }

        ConstructionRequestDto.ConstructionRequestDtoBuilder constructionRequestDto = ConstructionRequestDto.builder();

        constructionRequestDto.projectId( entityProjectId( entity ) );
        constructionRequestDto.constructionTypeId( entityConstructionTypeId( entity ) );
        constructionRequestDto.requestedByUserId( entityRequestedById( entity ) );
        constructionRequestDto.id( entity.getId() );
        constructionRequestDto.latitude( entity.getLatitude() );
        constructionRequestDto.longitude( entity.getLongitude() );
        constructionRequestDto.requestDate( entity.getRequestDate() );
        constructionRequestDto.requestStatus( entity.getRequestStatus() );
        constructionRequestDto.observations( entity.getObservations() );

        return constructionRequestDto.build();
    }

    private Long entityProjectId(ConstructionRequest constructionRequest) {
        if ( constructionRequest == null ) {
            return null;
        }
        Project project = constructionRequest.getProject();
        if ( project == null ) {
            return null;
        }
        Long id = project.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long entityConstructionTypeId(ConstructionRequest constructionRequest) {
        if ( constructionRequest == null ) {
            return null;
        }
        ConstructionType constructionType = constructionRequest.getConstructionType();
        if ( constructionType == null ) {
            return null;
        }
        Long id = constructionType.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long entityRequestedById(ConstructionRequest constructionRequest) {
        if ( constructionRequest == null ) {
            return null;
        }
        User requestedBy = constructionRequest.getRequestedBy();
        if ( requestedBy == null ) {
            return null;
        }
        Long id = requestedBy.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
