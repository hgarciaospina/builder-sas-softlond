package com.builderssas.api.mapper.role;

import com.builderssas.api.core.async.BaseMapper;
import com.builderssas.api.domain.model.user.Role;
import com.builderssas.api.domain.model.user.dto.RoleDto;
import com.builderssas.api.domain.model.user.dto.CreateRoleDto;
import com.builderssas.api.domain.model.user.dto.UpdateRoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper extends
        BaseMapper<Role, RoleDto, CreateRoleDto, UpdateRoleDto> {

    @Override RoleDto toDto(Role entity);
    @Override Role toEntity(CreateRoleDto dto);
    @Override void updateEntityFromDto(UpdateRoleDto dto, @MappingTarget Role entity);
    @Override List<RoleDto> toDtoList(List<Role> entities);
}
