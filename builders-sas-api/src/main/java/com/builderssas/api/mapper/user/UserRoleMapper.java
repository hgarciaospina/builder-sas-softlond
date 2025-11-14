package com.builderssas.api.mapper.userrole;

import com.builderssas.api.core.async.BaseMapper;
import com.builderssas.api.domain.model.user.UserRole;
import com.builderssas.api.domain.model.user.dto.UserRoleDto;
import com.builderssas.api.domain.model.user.dto.CreateUserRoleDto;
import com.builderssas.api.domain.model.user.dto.UpdateUserRoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserRoleMapper extends
        BaseMapper<UserRole, UserRoleDto, CreateUserRoleDto, UpdateUserRoleDto> {

    @Override
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "roleId", source = "role.id")
    UserRoleDto toDto(UserRole entity);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "role", ignore = true)
    UserRole toEntity(CreateUserRoleDto dto);

    @Override
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "role", ignore = true)
    void updateEntityFromDto(UpdateUserRoleDto dto, @MappingTarget UserRole entity);

    @Override
    List<UserRoleDto> toDtoList(List<UserRole> entities);
}
