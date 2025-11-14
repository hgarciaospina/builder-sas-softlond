package com.builderssas.api.core.async;

import org.mapstruct.MappingTarget;
import java.util.List;

/**
 * Generic contract for MapStruct mappers.
 * Standardizes conversions and allows reuse in generic async services.
 */
public interface BaseMapper<E, DTO, CreateDTO, UpdateDTO> {

    DTO toDto(E entity);

    E toEntity(CreateDTO dto);

    void updateEntityFromDto(UpdateDTO dto, @MappingTarget E entity);

    List<DTO> toDtoList(List<E> entities);
}
