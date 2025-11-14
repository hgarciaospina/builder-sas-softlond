package com.builderssas.api.mapper.material;

import com.builderssas.api.core.async.BaseMapper;
import com.builderssas.api.domain.model.material.MaterialType;
import com.builderssas.api.domain.model.material.dto.MaterialTypeDto;
import com.builderssas.api.domain.model.material.dto.CreateMaterialTypeDto;
import com.builderssas.api.domain.model.material.dto.UpdateMaterialTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MaterialTypeMapper extends
        BaseMapper<MaterialType, MaterialTypeDto, CreateMaterialTypeDto, UpdateMaterialTypeDto> {

    @Override MaterialTypeDto toDto(MaterialType entity);
    @Override MaterialType toEntity(CreateMaterialTypeDto dto);
    @Override void updateEntityFromDto(UpdateMaterialTypeDto dto, @MappingTarget MaterialType entity);
    @Override List<MaterialTypeDto> toDtoList(List<MaterialType> entities);
}
