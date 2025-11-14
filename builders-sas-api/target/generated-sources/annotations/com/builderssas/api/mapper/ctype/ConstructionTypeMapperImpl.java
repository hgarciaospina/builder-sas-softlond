package com.builderssas.api.mapper.ctype;

import com.builderssas.api.domain.model.construction.ConstructionType;
import com.builderssas.api.domain.model.construction.dto.ConstructionTypeDto;
import com.builderssas.api.domain.model.construction.dto.CreateConstructionTypeDto;
import com.builderssas.api.domain.model.construction.dto.UpdateConstructionTypeDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class ConstructionTypeMapperImpl implements ConstructionTypeMapper {

    @Override
    public ConstructionTypeDto toDto(ConstructionType entity) {
        if ( entity == null ) {
            return null;
        }

        ConstructionTypeDto.ConstructionTypeDtoBuilder constructionTypeDto = ConstructionTypeDto.builder();

        constructionTypeDto.id( entity.getId() );
        constructionTypeDto.name( entity.getName() );
        constructionTypeDto.durationDays( entity.getDurationDays() );
        constructionTypeDto.createdAt( entity.getCreatedAt() );

        return constructionTypeDto.build();
    }

    @Override
    public ConstructionType toEntity(CreateConstructionTypeDto dto) {
        if ( dto == null ) {
            return null;
        }

        ConstructionType.ConstructionTypeBuilder constructionType = ConstructionType.builder();

        constructionType.name( dto.getName() );
        constructionType.durationDays( dto.getDurationDays() );

        return constructionType.build();
    }

    @Override
    public void updateEntityFromDto(UpdateConstructionTypeDto dto, ConstructionType entity) {
        if ( dto == null ) {
            return;
        }

        entity.setName( dto.getName() );
        entity.setDurationDays( dto.getDurationDays() );
    }

    @Override
    public List<ConstructionTypeDto> toDtoList(List<ConstructionType> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ConstructionTypeDto> list = new ArrayList<ConstructionTypeDto>( entities.size() );
        for ( ConstructionType constructionType : entities ) {
            list.add( toDto( constructionType ) );
        }

        return list;
    }
}
