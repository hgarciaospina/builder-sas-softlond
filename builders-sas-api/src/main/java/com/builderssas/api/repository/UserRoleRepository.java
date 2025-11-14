package com.builderssas.api.repository;

import com.builderssas.api.domain.model.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    // ============================================================
    // Métodos existentes (se mantienen SIN CAMBIOS)
    // ============================================================

    boolean existsByUserIdAndRoleId(Long userId, Long roleId);

    List<UserRole> findByUserId(Long userId);

    List<UserRole> findByRoleId(Long roleId);


    // ============================================================
    // Nuevos métodos para cargar relaciones (JOIN FETCH)
    // No afectan los servicios existentes
    // ============================================================

    @Query("""
        SELECT ur
        FROM UserRole ur
        JOIN FETCH ur.user u
        JOIN FETCH ur.role r
    """)
    List<UserRole> findAllWithRelations();

    @Query("""
        SELECT ur
        FROM UserRole ur
        JOIN FETCH ur.user u
        JOIN FETCH ur.role r
        WHERE ur.id = :id
    """)
    Optional<UserRole> findByIdWithRelations(Long id);

}
