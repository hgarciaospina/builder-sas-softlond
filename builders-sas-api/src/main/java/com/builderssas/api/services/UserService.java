package com.builderssas.api.services;

import com.builderssas.api.domain.model.user.dto.LoginResponseDto;
import com.builderssas.api.domain.model.user.dto.UserDto;
import com.builderssas.api.domain.model.user.dto.CreateUserDto;
import com.builderssas.api.domain.model.user.dto.UpdateUserDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserService {

    CompletableFuture<UserDto> getById(Long id);

    CompletableFuture<List<UserDto>> getAll();

    CompletableFuture<UserDto> create(CreateUserDto dto);

    CompletableFuture<UserDto> update(Long id, UpdateUserDto dto);

    CompletableFuture<Void> delete(Long id);

    /** ✅ Login simple sin cifrado */
    CompletableFuture<LoginResponseDto> login(String username, String password);
}
