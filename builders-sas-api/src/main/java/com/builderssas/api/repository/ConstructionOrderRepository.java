package com.builderssas.api.repository;

import com.builderssas.api.domain.model.construction.ConstructionOrder;
import com.builderssas.api.domain.model.enums.OrderStatus;
import com.builderssas.api.repository.views.StatusCountView;
import com.builderssas.api.repository.views.TypeCountView;
import com.builderssas.api.repository.views.TypeStatusCountView;
import com.builderssas.api.repository.views.ProjectStatusCountView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConstructionOrderRepository extends JpaRepository<ConstructionOrder, Long> {

    // ============================================================
    // MÉTODOS EXISTENTES
    // ============================================================

    List<ConstructionOrder> findByProjectId(Long projectId);

    long countByProjectIdAndOrderStatus(Long projectId, OrderStatus status);

    @Query("""
        SELECT o
        FROM ConstructionOrder o
        WHERE o.project.id = :projectId
          AND o.orderStatus = :status
          AND o.scheduledStartDate = :scheduledStartDate
    """)
    List<ConstructionOrder> findScheduledByProjectAndDate(
            @Param("projectId") Long projectId,
            @Param("status") OrderStatus status,
            @Param("scheduledStartDate") LocalDate scheduledStartDate
    );

    // 👉 Órdenes con inicio EXACTO en un día (para “poner en progreso” en la mañana)
    List<ConstructionOrder> findByScheduledStartDate(LocalDate date);

    // 👉 Órdenes con fin EXACTO en un día (para “finalizar” a las 23:00)
    List<ConstructionOrder> findByScheduledEndDate(LocalDate date);

    // 👉 Proyectos con órdenes (para iterar por proyectos sin cruzar límites)
    @Query("SELECT DISTINCT o.project.id FROM ConstructionOrder o")
    List<Long> findAllProjectIdsWithOrders();

    // ✅ Total de órdenes del proyecto
    long countByProjectId(Long projectId);

    // ============================================================
    // ✅ CONSULTAS JOIN FETCH (SIN materialsConsumption)
    // ============================================================

    @Query("""
        SELECT o FROM ConstructionOrder o
        JOIN FETCH o.project p
        JOIN FETCH o.constructionType ct
        JOIN FETCH o.requestedBy u
        JOIN FETCH o.constructionRequest cr
    """)
    List<ConstructionOrder> findAllWithRelations();

    @Query("""
        SELECT o FROM ConstructionOrder o
        JOIN FETCH o.project p
        JOIN FETCH o.constructionType ct
        JOIN FETCH o.requestedBy u
        JOIN FETCH o.constructionRequest cr
        WHERE o.id = :id
    """)
    Optional<ConstructionOrder> findByIdWithRelations(@Param("id") Long id);

    @Query("""
        SELECT o
        FROM ConstructionOrder o
        JOIN FETCH o.project p
        JOIN FETCH o.constructionType ct
        JOIN FETCH o.requestedBy u
        JOIN FETCH o.constructionRequest cr
        WHERE p.id = :projectId
    """)
    List<ConstructionOrder> findByProjectIdWithRelations(@Param("projectId") Long projectId);

    @Query("""
        SELECT o
        FROM ConstructionOrder o
        JOIN FETCH o.project p
        JOIN FETCH o.constructionType ct
        JOIN FETCH o.requestedBy u
        JOIN FETCH o.constructionRequest cr
        WHERE o.orderStatus = :status
    """)
    List<ConstructionOrder> findByStatusWithRelations(@Param("status") OrderStatus status);

    @Query("""
        SELECT o
        FROM ConstructionOrder o
        JOIN FETCH o.project p
        JOIN FETCH o.constructionType ct
        JOIN FETCH o.requestedBy u
        JOIN FETCH o.constructionRequest cr
        WHERE p.id = :projectId
          AND o.orderStatus = :status
    """)
    List<ConstructionOrder> findByProjectIdAndStatusWithRelations(
            @Param("projectId") Long projectId,
            @Param("status") OrderStatus status
    );


    // ============================================================
    // ✅ MÉTRICAS (CORREGIDAS — GROUP BY)
    // ============================================================

    @Query("""
        SELECT o.orderStatus AS status, COUNT(o) AS count
        FROM ConstructionOrder o
        GROUP BY o.orderStatus
    """)
    List<StatusCountView> countAllGroupedByStatus();

    @Query("""
        SELECT ct.id AS constructionTypeId,
               ct.name AS constructionTypeName,
               COUNT(o) AS total
        FROM ConstructionOrder o
        JOIN o.constructionType ct
        GROUP BY ct.id, ct.name
    """)
    List<TypeCountView> countAllGroupedByConstructionType();

    @Query("""
        SELECT ct.id AS constructionTypeId,
               ct.name AS constructionTypeName,
               o.orderStatus AS status,
               COUNT(o) AS count
        FROM ConstructionOrder o
        JOIN o.constructionType ct
        GROUP BY ct.id, ct.name, o.orderStatus
    """)
    List<TypeStatusCountView> countAllGroupedByConstructionTypeAndStatus();

    @Query("""
        SELECT o.orderStatus AS status, COUNT(o) AS count
        FROM ConstructionOrder o
        WHERE o.project.id = :projectId
        GROUP BY o.orderStatus
    """)
    List<ProjectStatusCountView> countByProjectGroupedByStatus(@Param("projectId") Long projectId);

    @Query("""
        SELECT ct.id AS constructionTypeId,
               ct.name AS constructionTypeName,
               COUNT(o) AS total
        FROM ConstructionOrder o
        JOIN o.constructionType ct
        WHERE o.project.id = :projectId
        GROUP BY ct.id, ct.name
    """)
    List<TypeCountView> countByProjectGroupedByConstructionType(@Param("projectId") Long projectId);

    @Query("""
        SELECT ct.id AS constructionTypeId,
               ct.name AS constructionTypeName,
               o.orderStatus AS status,
               COUNT(o) AS count
        FROM ConstructionOrder o
        JOIN o.constructionType ct
        WHERE o.project.id = :projectId
        GROUP BY ct.id, ct.name, o.orderStatus
    """)
    List<TypeStatusCountView> countByProjectGroupedByConstructionTypeAndStatus(
            @Param("projectId") Long projectId
    );


    // ============================================================
    // VALIDACIÓN DE COORDENADAS
    // ============================================================

    boolean existsByLatitudeAndLongitude(Double latitude, Double longitude);

    /**
     * Obtiene la última fecha final (scheduledEndDate) registrada para un proyecto.
     *
     * Regla clave del dominio:
     *  - scheduledEndDate YA INCLUYE el día de entrega.
     *  - Para encadenar correctamente la siguiente construcción, se debe sumar +1 día:
     *        nextStart = MAX(scheduledEndDate) + 1
     *
     * Nota:
     *  - Si el proyecto no tiene órdenes previas, retorna Optional.empty().
     *  - Este método permite calcular fechas de forma más precisa y eficiente
     *    que buscar todas las órdenes y calcular el MAX en memoria.
     */
    @Query("""
    SELECT MAX(o.scheduledEndDate)
    FROM ConstructionOrder o
    WHERE o.project.id = :projectId
""")
    Optional<LocalDate> findLastEndDate(Long projectId);

    /**
     * Cuenta la cantidad TOTAL de órdenes asociadas a un proyecto.
     * Se usa para calcular el porcentaje de progreso del proyecto.
     */
    @Query("""
        SELECT COUNT(o)
        FROM ConstructionOrder o
        WHERE o.project.id = :projectId
    """)
    long countByProject(Long projectId);


    /**
     * Cuenta cuántas órdenes están en estado FINISHED
     * para el proyecto dado. Se usa para calcular:
     *
     *      progressPercentage = (finished / total) * 100
     */
    @Query("""
        SELECT COUNT(o)
        FROM ConstructionOrder o
        WHERE o.project.id = :projectId
          AND o.orderStatus = com.builderssas.api.domain.model.enums.OrderStatus.FINISHED
    """)
    long countFinishedByProject(Long projectId);


}
