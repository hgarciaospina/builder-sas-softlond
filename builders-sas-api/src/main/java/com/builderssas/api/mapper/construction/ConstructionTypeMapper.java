package com.builderssas.api.mapper.ctype;

import com.builderssas.api.core.async.BaseMapper;
import com.builderssas.api.domain.model.construction.ConstructionType;
import com.builderssas.api.domain.model.construction.dto.ConstructionTypeDto;
import com.builderssas.api.domain.model.construction.dto.CreateConstructionTypeDto;
import com.builderssas.api.domain.model.construction.dto.UpdateConstructionTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConstructionTypeMapper extends
        BaseMapper<ConstructionType, ConstructionTypeDto, CreateConstructionTypeDto, UpdateConstructionTypeDto> {

    @Override ConstructionTypeDto toDto(ConstructionType entity);
    @Override ConstructionType toEntity(CreateConstructionTypeDto dto);
    @Override void updateEntityFromDto(UpdateConstructionTypeDto dto, @MappingTarget ConstructionType entity);
    @Override List<ConstructionTypeDto> toDtoList(List<ConstructionType> entities);
}
