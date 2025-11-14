package com.builderssas.api.repository;

import com.builderssas.api.domain.model.construction.ConstructionRequest;
import com.builderssas.api.domain.model.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de Solicitudes de Construcción.
 *
 * Incluye consultas con JOIN FETCH para evitar LazyInitializationException.
 */
@Repository
public interface ConstructionRequestRepository extends JpaRepository<ConstructionRequest, Long> {

    // ============================================================
    // CONSULTAS BÁSICAS
    // ============================================================
    List<ConstructionRequest> findByProjectId(Long projectId);

    List<ConstructionRequest> findByRequestStatus(RequestStatus status);

    boolean existsByLatitudeAndLongitude(Double latitude, Double longitude);

    // ============================================================
    // CONSULTAS CON RELACIONES (JOIN FETCH)
    // ============================================================

    /**
     * Recupera todas las solicitudes con sus relaciones cargadas.
     */
    @Query("""
        SELECT r
        FROM ConstructionRequest r
        JOIN FETCH r.project p
        JOIN FETCH r.constructionType ct
        JOIN FETCH r.requestedBy u
    """)
    List<ConstructionRequest> findAllWithRelations();

    /**
     * Recupera una solicitud con todas sus relaciones por ID.
     */
    @Query("""
        SELECT r
        FROM ConstructionRequest r
        JOIN FETCH r.project p
        JOIN FETCH r.constructionType ct
        JOIN FETCH r.requestedBy u
        WHERE r.id = :id
    """)
    Optional<ConstructionRequest> findByIdWithRelations(@Param("id") Long id);

    /**
     * Recupera solicitudes filtradas por proyecto con todas las relaciones cargadas.
     */
    @Query("""
        SELECT r
        FROM ConstructionRequest r
        JOIN FETCH r.project p
        JOIN FETCH r.constructionType ct
        JOIN FETCH r.requestedBy u
        WHERE p.id = :projectId
    """)
    List<ConstructionRequest> findByProjectIdWithRelations(@Param("projectId") Long projectId);

    /**
     * Recupera solicitudes filtradas por estado con todas las relaciones cargadas.
     */
    @Query("""
        SELECT r
        FROM ConstructionRequest r
        JOIN FETCH r.project p
        JOIN FETCH r.constructionType ct
        JOIN FETCH r.requestedBy u
        WHERE r.requestStatus = :status
    """)
    List<ConstructionRequest> findByRequestStatusWithRelations(@Param("status") RequestStatus status);
}
