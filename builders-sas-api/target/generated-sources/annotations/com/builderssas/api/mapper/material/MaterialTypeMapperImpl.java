package com.builderssas.api.mapper.material;

import com.builderssas.api.domain.model.material.MaterialType;
import com.builderssas.api.domain.model.material.dto.CreateMaterialTypeDto;
import com.builderssas.api.domain.model.material.dto.MaterialTypeDto;
import com.builderssas.api.domain.model.material.dto.UpdateMaterialTypeDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class MaterialTypeMapperImpl implements MaterialTypeMapper {

    @Override
    public MaterialTypeDto toDto(MaterialType entity) {
        if ( entity == null ) {
            return null;
        }

        MaterialTypeDto.MaterialTypeDtoBuilder materialTypeDto = MaterialTypeDto.builder();

        materialTypeDto.id( entity.getId() );
        materialTypeDto.code( entity.getCode() );
        materialTypeDto.name( entity.getName() );
        materialTypeDto.unit( entity.getUnit() );
        if ( entity.getStock() != null ) {
            materialTypeDto.stock( entity.getStock() );
        }

        return materialTypeDto.build();
    }

    @Override
    public MaterialType toEntity(CreateMaterialTypeDto dto) {
        if ( dto == null ) {
            return null;
        }

        MaterialType.MaterialTypeBuilder materialType = MaterialType.builder();

        materialType.code( dto.getCode() );
        materialType.name( dto.getName() );
        materialType.unit( dto.getUnit() );
        materialType.stock( dto.getStock() );

        return materialType.build();
    }

    @Override
    public void updateEntityFromDto(UpdateMaterialTypeDto dto, MaterialType entity) {
        if ( dto == null ) {
            return;
        }

        entity.setName( dto.getName() );
        entity.setUnit( dto.getUnit() );
        entity.setStock( dto.getStock() );
    }

    @Override
    public List<MaterialTypeDto> toDtoList(List<MaterialType> entities) {
        if ( entities == null ) {
            return null;
        }

        List<MaterialTypeDto> list = new ArrayList<MaterialTypeDto>( entities.size() );
        for ( MaterialType materialType : entities ) {
            list.add( toDto( materialType ) );
        }

        return list;
    }
}
