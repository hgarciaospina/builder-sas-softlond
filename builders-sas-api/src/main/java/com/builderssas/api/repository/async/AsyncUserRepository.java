package com.builderssas.api.repository.async;

import com.builderssas.api.domain.model.user.User;
import com.builderssas.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * AsyncUserRepository — manejo asincrónico y transaccional seguro para UserRepository.
 *
 * ❌ Sin supplyAsync() (evita pérdida de contexto transaccional)
 * ✅ Usa @Async + completedFuture() para mantener la sesión activa dentro del proxy Spring.
 */
@Repository
@RequiredArgsConstructor
public class AsyncUserRepository {

    private final UserRepository userRepository;

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<Optional<User>> findByIdAsync(Long id) {
        return CompletableFuture.completedFuture(userRepository.findById(id));
    }

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<List<User>> findAllAsync() {
        return CompletableFuture.completedFuture(userRepository.findAll());
    }

    @Async
    @Transactional
    public CompletableFuture<User> saveAsync(User user) {
        // ✅ Se ejecuta dentro del hilo del proxy transaccional, no en supplyAsync
        return CompletableFuture.completedFuture(userRepository.save(user));
    }

    @Async
    @Transactional
    public CompletableFuture<Void> deleteByIdAsync(Long id) {
        userRepository.deleteById(id);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<Boolean> existsByIdAsync(Long id) {
        return CompletableFuture.completedFuture(userRepository.existsById(id));
    }
}
