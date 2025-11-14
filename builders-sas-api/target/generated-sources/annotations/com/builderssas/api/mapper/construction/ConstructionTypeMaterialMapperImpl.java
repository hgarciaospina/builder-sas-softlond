package com.builderssas.api.mapper.construction;

import com.builderssas.api.domain.model.construction.ConstructionType;
import com.builderssas.api.domain.model.construction.ConstructionTypeMaterial;
import com.builderssas.api.domain.model.construction.dto.ConstructionTypeMaterialDto;
import com.builderssas.api.domain.model.construction.dto.CreateConstructionTypeMaterialDto;
import com.builderssas.api.domain.model.construction.dto.UpdateConstructionTypeMaterialDto;
import com.builderssas.api.domain.model.material.MaterialType;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class ConstructionTypeMaterialMapperImpl implements ConstructionTypeMaterialMapper {

    @Override
    public List<ConstructionTypeMaterialDto> toDtoList(List<ConstructionTypeMaterial> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ConstructionTypeMaterialDto> list = new ArrayList<ConstructionTypeMaterialDto>( entities.size() );
        for ( ConstructionTypeMaterial constructionTypeMaterial : entities ) {
            list.add( toDto( constructionTypeMaterial ) );
        }

        return list;
    }

    @Override
    public ConstructionTypeMaterialDto toDto(ConstructionTypeMaterial entity) {
        if ( entity == null ) {
            return null;
        }

        ConstructionTypeMaterialDto.ConstructionTypeMaterialDtoBuilder constructionTypeMaterialDto = ConstructionTypeMaterialDto.builder();

        constructionTypeMaterialDto.constructionTypeId( entityConstructionTypeId( entity ) );
        constructionTypeMaterialDto.constructionTypeName( entityConstructionTypeName( entity ) );
        constructionTypeMaterialDto.materialTypeId( entityMaterialTypeId( entity ) );
        constructionTypeMaterialDto.materialTypeCode( entityMaterialTypeCode( entity ) );
        constructionTypeMaterialDto.materialTypeName( entityMaterialTypeName( entity ) );
        constructionTypeMaterialDto.materialUnit( entityMaterialTypeUnit( entity ) );
        constructionTypeMaterialDto.id( entity.getId() );
        if ( entity.getQuantityRequired() != null ) {
            constructionTypeMaterialDto.quantityRequired( entity.getQuantityRequired().intValue() );
        }

        return constructionTypeMaterialDto.build();
    }

    @Override
    public ConstructionTypeMaterial toEntity(CreateConstructionTypeMaterialDto dto) {
        if ( dto == null ) {
            return null;
        }

        ConstructionTypeMaterial.ConstructionTypeMaterialBuilder constructionTypeMaterial = ConstructionTypeMaterial.builder();

        if ( dto.getQuantityRequired() != null ) {
            constructionTypeMaterial.quantityRequired( dto.getQuantityRequired().doubleValue() );
        }

        return constructionTypeMaterial.build();
    }

    @Override
    public void updateEntityFromDto(UpdateConstructionTypeMaterialDto dto, ConstructionTypeMaterial entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getQuantityRequired() != null ) {
            entity.setQuantityRequired( dto.getQuantityRequired().doubleValue() );
        }
        else {
            entity.setQuantityRequired( null );
        }
    }

    private Long entityConstructionTypeId(ConstructionTypeMaterial constructionTypeMaterial) {
        if ( constructionTypeMaterial == null ) {
            return null;
        }
        ConstructionType constructionType = constructionTypeMaterial.getConstructionType();
        if ( constructionType == null ) {
            return null;
        }
        Long id = constructionType.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityConstructionTypeName(ConstructionTypeMaterial constructionTypeMaterial) {
        if ( constructionTypeMaterial == null ) {
            return null;
        }
        ConstructionType constructionType = constructionTypeMaterial.getConstructionType();
        if ( constructionType == null ) {
            return null;
        }
        String name = constructionType.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Long entityMaterialTypeId(ConstructionTypeMaterial constructionTypeMaterial) {
        if ( constructionTypeMaterial == null ) {
            return null;
        }
        MaterialType materialType = constructionTypeMaterial.getMaterialType();
        if ( materialType == null ) {
            return null;
        }
        Long id = materialType.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityMaterialTypeCode(ConstructionTypeMaterial constructionTypeMaterial) {
        if ( constructionTypeMaterial == null ) {
            return null;
        }
        MaterialType materialType = constructionTypeMaterial.getMaterialType();
        if ( materialType == null ) {
            return null;
        }
        String code = materialType.getCode();
        if ( code == null ) {
            return null;
        }
        return code;
    }

    private String entityMaterialTypeName(ConstructionTypeMaterial constructionTypeMaterial) {
        if ( constructionTypeMaterial == null ) {
            return null;
        }
        MaterialType materialType = constructionTypeMaterial.getMaterialType();
        if ( materialType == null ) {
            return null;
        }
        String name = materialType.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String entityMaterialTypeUnit(ConstructionTypeMaterial constructionTypeMaterial) {
        if ( constructionTypeMaterial == null ) {
            return null;
        }
        MaterialType materialType = constructionTypeMaterial.getMaterialType();
        if ( materialType == null ) {
            return null;
        }
        String unit = materialType.getUnit();
        if ( unit == null ) {
            return null;
        }
        return unit;
    }
}
