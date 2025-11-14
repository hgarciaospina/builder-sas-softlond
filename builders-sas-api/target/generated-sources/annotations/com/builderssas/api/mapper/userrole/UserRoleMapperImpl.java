package com.builderssas.api.mapper.userrole;

import com.builderssas.api.domain.model.user.Role;
import com.builderssas.api.domain.model.user.User;
import com.builderssas.api.domain.model.user.UserRole;
import com.builderssas.api.domain.model.user.dto.CreateUserRoleDto;
import com.builderssas.api.domain.model.user.dto.UpdateUserRoleDto;
import com.builderssas.api.domain.model.user.dto.UserRoleDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class UserRoleMapperImpl implements UserRoleMapper {

    @Override
    public UserRoleDto toDto(UserRole entity) {
        if ( entity == null ) {
            return null;
        }

        UserRoleDto.UserRoleDtoBuilder userRoleDto = UserRoleDto.builder();

        userRoleDto.userId( entityUserId( entity ) );
        userRoleDto.roleId( entityRoleId( entity ) );
        userRoleDto.id( entity.getId() );
        userRoleDto.assignedAt( entity.getAssignedAt() );

        return userRoleDto.build();
    }

    @Override
    public UserRole toEntity(CreateUserRoleDto dto) {
        if ( dto == null ) {
            return null;
        }

        UserRole.UserRoleBuilder userRole = UserRole.builder();

        userRole.assignedAt( dto.getAssignedAt() );

        return userRole.build();
    }

    @Override
    public void updateEntityFromDto(UpdateUserRoleDto dto, UserRole entity) {
        if ( dto == null ) {
            return;
        }

        entity.setAssignedAt( dto.getAssignedAt() );
    }

    @Override
    public List<UserRoleDto> toDtoList(List<UserRole> entities) {
        if ( entities == null ) {
            return null;
        }

        List<UserRoleDto> list = new ArrayList<UserRoleDto>( entities.size() );
        for ( UserRole userRole : entities ) {
            list.add( toDto( userRole ) );
        }

        return list;
    }

    private Long entityUserId(UserRole userRole) {
        if ( userRole == null ) {
            return null;
        }
        User user = userRole.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long entityRoleId(UserRole userRole) {
        if ( userRole == null ) {
            return null;
        }
        Role role = userRole.getRole();
        if ( role == null ) {
            return null;
        }
        Long id = role.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
