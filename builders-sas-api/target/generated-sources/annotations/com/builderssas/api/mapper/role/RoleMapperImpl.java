package com.builderssas.api.mapper.role;

import com.builderssas.api.domain.model.user.Role;
import com.builderssas.api.domain.model.user.dto.CreateRoleDto;
import com.builderssas.api.domain.model.user.dto.RoleDto;
import com.builderssas.api.domain.model.user.dto.UpdateRoleDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleDto toDto(Role entity) {
        if ( entity == null ) {
            return null;
        }

        RoleDto.RoleDtoBuilder roleDto = RoleDto.builder();

        roleDto.id( entity.getId() );
        roleDto.name( entity.getName() );
        roleDto.description( entity.getDescription() );

        return roleDto.build();
    }

    @Override
    public Role toEntity(CreateRoleDto dto) {
        if ( dto == null ) {
            return null;
        }

        Role.RoleBuilder role = Role.builder();

        role.name( dto.getName() );
        role.description( dto.getDescription() );

        return role.build();
    }

    @Override
    public void updateEntityFromDto(UpdateRoleDto dto, Role entity) {
        if ( dto == null ) {
            return;
        }

        entity.setName( dto.getName() );
        entity.setDescription( dto.getDescription() );
    }

    @Override
    public List<RoleDto> toDtoList(List<Role> entities) {
        if ( entities == null ) {
            return null;
        }

        List<RoleDto> list = new ArrayList<RoleDto>( entities.size() );
        for ( Role role : entities ) {
            list.add( toDto( role ) );
        }

        return list;
    }
}
