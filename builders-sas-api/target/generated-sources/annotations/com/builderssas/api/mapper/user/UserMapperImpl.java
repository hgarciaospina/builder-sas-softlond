package com.builderssas.api.mapper.user;

import com.builderssas.api.domain.model.user.User;
import com.builderssas.api.domain.model.user.dto.CreateUserDto;
import com.builderssas.api.domain.model.user.dto.UpdateUserDto;
import com.builderssas.api.domain.model.user.dto.UserDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User entity) {
        if ( entity == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.id( entity.getId() );
        userDto.username( entity.getUsername() );
        userDto.firstname( entity.getFirstname() );
        userDto.lastname( entity.getLastname() );
        userDto.email( entity.getEmail() );
        userDto.password( entity.getPassword() );
        userDto.active( entity.getActive() );

        return userDto.build();
    }

    @Override
    public User toEntity(CreateUserDto dto) {
        if ( dto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.username( dto.getUsername() );
        user.firstname( dto.getFirstname() );
        user.lastname( dto.getLastname() );
        user.email( dto.getEmail() );
        user.password( dto.getPassword() );
        user.active( dto.getActive() );

        return user.build();
    }

    @Override
    public void updateEntityFromDto(UpdateUserDto dto, User entity) {
        if ( dto == null ) {
            return;
        }

        entity.setUsername( dto.getUsername() );
        entity.setFirstname( dto.getFirstname() );
        entity.setLastname( dto.getLastname() );
        entity.setEmail( dto.getEmail() );
        entity.setPassword( dto.getPassword() );
        entity.setActive( dto.getActive() );
    }

    @Override
    public List<UserDto> toDtoList(List<User> entities) {
        if ( entities == null ) {
            return null;
        }

        List<UserDto> list = new ArrayList<UserDto>( entities.size() );
        for ( User user : entities ) {
            list.add( toDto( user ) );
        }

        return list;
    }
}
