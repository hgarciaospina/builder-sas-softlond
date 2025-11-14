package com.builderssas.api.services.impl;

import com.builderssas.api.domain.model.user.dto.LoginResponseDto;
import com.builderssas.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;

    public CompletableFuture<LoginResponseDto> login(String username, String password) {

        return CompletableFuture.supplyAsync(() ->
                userRepo.findByUsername(username)
                        .filter(user -> user.getPassword().equals(password))
                        .map(user ->
                                LoginResponseDto.builder()
                                        .id(user.getId())
                                        .username(user.getUsername())
                                        .firstname(user.getFirstname())
                                        .lastname(user.getLastname())
                                        .email(user.getEmail())
                                        .roles(
                                                user.getRoles()
                                                        .stream()
                                                        .map(r -> r.getRole().getName())
                                                        .toList()
                                        )
                                        .build()
                        )
                        .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"))
        );
    }
}
