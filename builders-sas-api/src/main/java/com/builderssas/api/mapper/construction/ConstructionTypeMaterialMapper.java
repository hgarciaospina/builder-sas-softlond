package com.builderssas.api.mapper.construction;

import com.builderssas.api.core.async.BaseMapper;
import com.builderssas.api.domain.model.construction.ConstructionTypeMaterial;
import com.builderssas.api.domain.model.construction.dto.ConstructionTypeMaterialDto;
import com.builderssas.api.domain.model.construction.dto.CreateConstructionTypeMaterialDto;
import com.builderssas.api.domain.model.construction.dto.UpdateConstructionTypeMaterialDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ConstructionTypeMaterialMapper
        extends BaseMapper<ConstructionTypeMaterial,
        ConstructionTypeMaterialDto,
        CreateConstructionTypeMaterialDto,
        UpdateConstructionTypeMaterialDto> {

    // ENTITY → DTO
    @Mapping(source = "constructionType.id", target = "constructionTypeId")
    @Mapping(source = "constructionType.name", target = "constructionTypeName")
    @Mapping(source = "materialType.id", target = "materialTypeId")
    @Mapping(source = "materialType.code", target = "materialTypeCode")
    @Mapping(source = "materialType.name", target = "materialTypeName")
    @Mapping(source = "materialType.unit", target = "materialUnit")
    ConstructionTypeMaterialDto toDto(ConstructionTypeMaterial entity);

    // CREATE DTO → ENTITY
    @Mapping(target = "constructionType", ignore = true)
    @Mapping(target = "materialType", ignore = true)
    ConstructionTypeMaterial toEntity(CreateConstructionTypeMaterialDto dto);

    // UPDATE DTO → ENTITY ✅ NECESARIO
    @Mapping(target = "constructionType", ignore = true)
    @Mapping(target = "materialType", ignore = true)
    void updateEntityFromDto(UpdateConstructionTypeMaterialDto dto,
                             @MappingTarget ConstructionTypeMaterial entity);
}
