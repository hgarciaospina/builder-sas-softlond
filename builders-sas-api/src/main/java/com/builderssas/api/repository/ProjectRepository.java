package com.builderssas.api.repository;

import com.builderssas.api.domain.model.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // ============================================================
    // Metodos existentes (se mantienen sin modificaciones)
    // ============================================================

    boolean existsByNameIgnoreCase(String name);

    @Query("""
        SELECT COUNT(o) > 0
        FROM ConstructionOrder o
        WHERE o.project.id = :projectId
    """)
    boolean hasOrders(@Param("projectId") Long projectId);

    @Query("""
        SELECT COUNT(r) > 0
        FROM ConstructionRequest r
        WHERE r.project.id = :projectId
    """)
    boolean hasRequests(@Param("projectId") Long projectId);

    // ============================================================
    // Nuevos metodos para cargar relaciones (JOIN FETCH)
    // ============================================================

    @Query("""
        SELECT p FROM Project p
        JOIN FETCH p.createdBy u
    """)
    List<Project> findAllWithRelations();

    @Query("""
        SELECT p FROM Project p
        JOIN FETCH p.createdBy u
        WHERE p.id = :id
    """)
    Optional<Project> findByIdWithRelations(Long id);

}
