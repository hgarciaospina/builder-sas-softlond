package com.builderssas.api.repository;

import com.builderssas.api.domain.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // ============================================================
    // Método existente (se mantiene EXACTAMENTE como está)
    // ============================================================

    boolean existsByNameIgnoreCase(String name);


    // ============================================================
    // Nuevos métodos para cargar relaciones (JOIN FETCH)
    // ============================================================

    @Query("SELECT r FROM Role r")
    List<Role> findAllWithRelations();

    @Query("SELECT r FROM Role r WHERE r.id = :id")
    Optional<Role> findByIdWithRelations(Long id);

}
