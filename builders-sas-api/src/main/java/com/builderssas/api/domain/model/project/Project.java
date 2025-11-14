package com.builderssas.api.domain.model.project;

import com.builderssas.api.domain.model.construction.ConstructionOrder;
import com.builderssas.api.domain.model.construction.ConstructionRequest;
import com.builderssas.api.domain.model.enums.ProjectStatus;
import com.builderssas.api.domain.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * ============================================================================
 *                                ENTIDAD: PROJECT
 * ============================================================================
 *
 * Representa un proyecto de construcción dentro del sistema "Constructores S.A.S".
 * Un Proyecto agrupa todas las solicitudes (ConstructionRequest) y órdenes
 * de construcción (ConstructionOrder) asociadas a un desarrollo urbanístico.
 *
 * ----------------------------------------------------------------------------
 *  TRAZABILIDAD → createdBy
 * ----------------------------------------------------------------------------
 * • Todo proyecto debe registrar el usuario (arquitecto) que lo creó.
 * • Este campo habilita:
 *      - Auditoría completa
 *      - Control de permisos por usuario
 *      - Métricas de productividad del arquitecto
 *      - Notificaciones y seguimiento funcional
 *
 * • Este comportamiento es totalmente consistente con:
 *      - ConstructionRequest → requestedBy
 *      - ConstructionOrder  → requestedBy
 *
 * ----------------------------------------------------------------------------
 *  FECHAS REALES DEL PROYECTO
 * ----------------------------------------------------------------------------
 * • projectStartDate → Se asigna automáticamente cuando la PRIMERA orden
 *   del proyecto pasa a IN_PROGRESS (cron AM).
 *
 * • projectEndDate → Se asigna cuando la ÚLTIMA orden pasa a FINISHED (cron PM).
 *
 * Estos valores se calculan desde los servicios, no desde la entidad.
 *
 * ----------------------------------------------------------------------------
 *  CÁLCULO DEL AVANCE DEL PROYECTO (SERVICIOS)
 * ----------------------------------------------------------------------------
 *      progress = (ordenes_finalizadas / total_ordenes) * 100
 *
 * El cálculo se realiza desde ProjectService según el estado
 * de las ConstructionOrder.
 *
 * ----------------------------------------------------------------------------
 *  RELACIONES
 * ----------------------------------------------------------------------------
 * Project es la entidad PADRE:
 *   - construction_orders.project_id
 *   - construction_requests.project_id
 * contienen las FOREIGN KEY reales.
 *
 * Esta entidad NO almacena claves foráneas adicionales, solo recibe navegación.
 *
 * ----------------------------------------------------------------------------
 *  RELACIONES DE NAVEGACIÓN (NO CRUD)
 * ----------------------------------------------------------------------------
 * Las colecciones 'orders' y 'requests' existen únicamente para:
 *   • Navegación interna de JPA
 *   • Consultas desde servicios
 *   • Cálculo del estado global del proyecto
 *   • Visualización en el frontend
 *
 * Estas relaciones NO deben utilizarse para CRUD desde Project.
 *
 * ----------------------------------------------------------------------------
 *  ÍNDICES
 * ----------------------------------------------------------------------------
 * Se incluyen índices diseñados con criterios empresariales:
 *   • Nombre del proyecto
 *   • Estado del proyecto
 *   • Usuario creador
 *   • (Opcional) Fecha de inicio del proyecto
 *
 * ----------------------------------------------------------------------------
 *  PROGRAMACIÓN FUNCIONAL → @With
 * ----------------------------------------------------------------------------
 * Se añade @With para permitir copias inmutables del objeto en flujos
 * asincrónicos y funcionales (CompletableFuture, pipelines, etc.).
 *
 * ============================================================================
 */
@Entity
@Table(
        name = "projects",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_projects_name", columnNames = "name")
        },
        indexes = {
                @Index(name = "idx_projects_name", columnList = "name"),
                @Index(name = "idx_projects_status", columnList = "project_status"),
                @Index(name = "idx_projects_created_by", columnList = "created_by_user_id"),
                // Índice opcional útil para cronogramas y análisis
                @Index(name = "idx_projects_start_date", columnList = "project_start_date")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@With
public class Project {

    /** Identificador único del proyecto. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Nombre único del proyecto (ej.: "Ciudadela del Futuro"). */
    @Column(nullable = false, length = 200, unique = true)
    private String name;

    /** Descripción detallada del proyecto. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Usuario (arquitecto) que creó el proyecto. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "created_by_user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_project_created_by_user",
                    foreignKeyDefinition = "FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE RESTRICT"
            )
    )
    private User createdBy;

    /** Fecha real en la que se inicia el proyecto (derivada del estado de las órdenes). */
    @Column(name = "project_start_date")
    private LocalDate projectStartDate;

    /** Fecha real en la que se finaliza el proyecto. */
    @Column(name = "project_end_date")
    private LocalDate projectEndDate;

    /** Porcentaje de avance general del proyecto (0–100). */
    @Column(name = "progress_percentage", nullable = false)
    @Builder.Default
    private Double progressPercentage = 0.0;

    /** Estado actual del proyecto. */
    @Enumerated(EnumType.STRING)
    @Column(name = "project_status", nullable = false, length = 20)
    @Builder.Default
    private ProjectStatus projectStatus = ProjectStatus.PLANNED;

    /** Órdenes asociadas a este proyecto (relación de navegación). */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ConstructionOrder> orders = new HashSet<>();

    /** Solicitudes asociadas a este proyecto (relación de navegación). */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ConstructionRequest> requests = new HashSet<>();
}
