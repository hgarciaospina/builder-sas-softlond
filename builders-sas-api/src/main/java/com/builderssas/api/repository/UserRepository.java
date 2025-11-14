package com.builderssas.api.repository;

import com.builderssas.api.domain.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // ============================================================
    // MÉTODO EXISTENTE — SE MANTIENE EXACTO
    // ============================================================

    Optional<User> findByUsernameIgnoreCase(String username);


    // ============================================================
    // NUEVOS MÉTODOS PARA CARGAR RELACIONES (JOIN FETCH)
    // NO AFECTAN TUS SERVICIOS
    // NO SE INVOCAN AUTOMÁTICAMENTE
    // NO ROMPEN NINGÚN CONTRATO
    // ============================================================

    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.roles r
    """)
    List<User> findAllWithRelations();

    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.roles r
        WHERE u.id = :id
    """)
    Optional<User> findByIdWithRelations(Long id);

    @Query("""
        SELECT u
        FROM User u
        LEFT JOIN FETCH u.roles r
        WHERE u.username = :username
    """)
    Optional<User> findByUsernameWithRoles(@Param("username") String username);
    Optional<User> findByUsername(String username);
}
