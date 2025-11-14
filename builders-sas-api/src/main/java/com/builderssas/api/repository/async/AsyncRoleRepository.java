package com.builderssas.api.repository.async;

import com.builderssas.api.domain.model.user.Role;
import com.builderssas.api.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * ✅ AsyncRoleRepository — implementación no bloqueante REAL.
 * Ejecuta todas las operaciones JPA en un pool de hilos separado (asyncExecutor).
 * Evita @Async para no perder el contexto de proxy de Spring.
 */
@Repository
@RequiredArgsConstructor
public class AsyncRoleRepository {

    private final RoleRepository roleRepository;
    private final Executor asyncExecutor;

    @Transactional(readOnly = true)
    public CompletableFuture<Optional<Role>> findByIdAsync(Long id) {
        return CompletableFuture.supplyAsync(() -> roleRepository.findById(id), asyncExecutor);
    }

    @Transactional(readOnly = true)
    public CompletableFuture<List<Role>> findAllAsync() {
        return CompletableFuture.supplyAsync(roleRepository::findAll, asyncExecutor);
    }

    @Transactional
    public CompletableFuture<Role> saveAsync(Role role) {
        return CompletableFuture.supplyAsync(() -> roleRepository.save(role), asyncExecutor);
    }

    @Transactional
    public CompletableFuture<Void> deleteByIdAsync(Long id) {
        return CompletableFuture.runAsync(() -> roleRepository.deleteById(id), asyncExecutor);
    }

    @Transactional(readOnly = true)
    public CompletableFuture<Boolean> existsByIdAsync(Long id) {
        return CompletableFuture.supplyAsync(() -> roleRepository.existsById(id), asyncExecutor);
    }
}
