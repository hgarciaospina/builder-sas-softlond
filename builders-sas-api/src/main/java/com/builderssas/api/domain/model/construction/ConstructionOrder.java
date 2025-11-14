package com.builderssas.api.domain.model.construction;

import com.builderssas.api.domain.model.constructionorder.dto.MaterialConsumptionDto;
import com.builderssas.api.domain.model.enums.OrderStatus;
import com.builderssas.api.domain.model.project.Project;
import com.builderssas.api.domain.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================================
 *                          ENTIDAD: CONSTRUCTION_ORDER
 * ============================================================================
 *
 * Representa una Orden de Construcción generada automáticamente a partir de una
 * solicitud aprobada (ConstructionRequest).
 *
 * Una ConstructionOrder define la ejecución real de una construcción dentro
 * de un proyecto, con fechas programadas calculadas por el CRON y un flujo de
 * estados estrictamente controlado:
 *
 *      PENDING → IN_PROGRESS → FINISHED
 *
 * ----------------------------------------------------------------------------
 *  REGLAS DE DOMINIO PRINCIPALES
 * ----------------------------------------------------------------------------
 * • La orden nace inmediatamente cuando una solicitud es aprobada.
 * • Las fechas scheduled_start_date y scheduled_end_date son calculadas
 *   automáticamente por tareas programadas (CRON AM/PM).
 * • Las coordenadas deben ser únicas dentro del proyecto.
 * • La orden referencia a:
 *      - La solicitud original
 *      - El proyecto
 *      - El tipo de construcción
 *      - El usuario solicitante
 *
 * ----------------------------------------------------------------------------
 *  AUDITORÍA
 * ----------------------------------------------------------------------------
 * • createdAt  → se asigna cuando la orden se crea en el servicio.
 * • updatedAt  → se actualiza cada vez que el CRON o el servicio cambia estado.
 *
 * Esto permite trazabilidad empresarial sin intervención manual.
 *
 * ----------------------------------------------------------------------------
 *  ÍNDICES Y RESTRICCIONES
 * ----------------------------------------------------------------------------
 * • Índices en estado, fecha de solicitud y proyecto para optimizar:
 *      - Cron de actualización
 *      - Consultas masivas
 *      - Dashboards
 * • UniqueConstraint sobre (project_id, latitude, longitude) para garantizar
 *   que cada proyecto tenga coordenadas únicas para cada construcción.
 *
 * ============================================================================
 */
@Entity
@Table(
        name = "construction_orders",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_orders_project_coordinates",
                        columnNames = {"project_id", "latitude", "longitude"}
                )
        },
        indexes = {
                @Index(name = "idx_orders_status", columnList = "order_status"),
                @Index(name = "idx_orders_requested_date", columnList = "requested_date"),
                @Index(name = "idx_orders_project", columnList = "project_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@With
public class ConstructionOrder {

    /** Identificador único de la orden de construcción. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Solicitud aprobada de la cual nace esta orden. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "construction_request_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_orders_request",
                    foreignKeyDefinition =
                            "FOREIGN KEY (construction_request_id) REFERENCES construction_requests(id) ON DELETE RESTRICT"
            )
    )
    private ConstructionRequest constructionRequest;

    /** Proyecto al cual pertenece esta orden. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "project_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_orders_project",
                    foreignKeyDefinition =
                            "FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE RESTRICT"
            )
    )
    private Project project;

    /** Tipo de construcción que se ejecutará (casa, edificio, lago, etc.). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "construction_type_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_orders_construction_type",
                    foreignKeyDefinition =
                            "FOREIGN KEY (construction_type_id) REFERENCES construction_types(id) ON DELETE RESTRICT"
            )
    )
    private ConstructionType constructionType;

    /** Usuario que registró la solicitud original. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "requested_by_user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_orders_requested_user",
                    foreignKeyDefinition =
                            "FOREIGN KEY (requested_by_user_id) REFERENCES users(id) ON DELETE RESTRICT"
            )
    )
    private User requestedBy;

    /** Coordenada latitud única dentro del proyecto. */
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    /** Coordenada longitud única dentro del proyecto. */
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    /** Fecha en que se originó la orden (copiada desde la solicitud). */
    @Column(name = "requested_date", nullable = false)
    private LocalDate requestedDate;

    /**
     * Fecha programada de inicio.
     * Calculada automáticamente por el CRON AM.
     */
    @Column(name = "scheduled_start_date")
    private LocalDate scheduledStartDate;

    /**
     * Fecha programada de finalización.
     * Calculada automáticamente por el CRON PM.
     */
    @Column(name = "scheduled_end_date")
    private LocalDate scheduledEndDate;

    /** Estado actual de la orden: PENDING → IN_PROGRESS → FINISHED. */
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 20)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.PENDING;

    /** Fecha y hora en que se creó la orden. */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Fecha y hora del último cambio de estado. */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Auditoría automática al crear la orden. */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    /** Auditoría automática al actualizar la orden. */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Column(name = "observations", length = 2000)
    private String observations;

    @Transient
    private List<MaterialConsumptionDto> materialsConsumption;

}
