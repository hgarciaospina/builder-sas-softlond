package com.builderssas.api.mapper.user;

import com.builderssas.api.core.async.BaseMapper;
import com.builderssas.api.domain.model.user.User;
import com.builderssas.api.domain.model.user.dto.UserDto;
import com.builderssas.api.domain.model.user.dto.CreateUserDto;
import com.builderssas.api.domain.model.user.dto.UpdateUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper extends
        BaseMapper<User, UserDto, CreateUserDto, UpdateUserDto> {

    @Override UserDto toDto(User entity);
    @Override User toEntity(CreateUserDto dto);
    @Override void updateEntityFromDto(UpdateUserDto dto, @MappingTarget User entity);
    @Override List<UserDto> toDtoList(List<User> entities);
}
