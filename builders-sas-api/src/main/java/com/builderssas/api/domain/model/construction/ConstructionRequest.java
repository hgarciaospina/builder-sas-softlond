package com.builderssas.api.domain.model.construction;

import com.builderssas.api.domain.model.enums.RequestStatus;
import com.builderssas.api.domain.model.project.Project;
import com.builderssas.api.domain.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Optional;

/**
 * ============================================================================
 *                         ENTIDAD: CONSTRUCTION_REQUEST
 * ============================================================================
 *
 * Representa una solicitud de construcción registrada por un arquitecto dentro
 * de un Proyecto. Es el punto de partida del flujo funcional:
 *
 *      Solicitud → Validación → Aprobación/Rechazo → Creación de Orden
 *
 * ----------------------------------------------------------------------------
 *  RELACIÓN CON OTRAS ENTIDADES
 * ----------------------------------------------------------------------------
 * • Project (obligatoria): indica a qué proyecto pertenece la solicitud.
 * • ConstructionType (obligatoria): tipo de construcción solicitada.
 * • User requestedBy (obligatoria): el arquitecto que generó la solicitud.
 *
 * ----------------------------------------------------------------------------
 *  COORDENADAS
 * ----------------------------------------------------------------------------
 * Las solicitudes NO consumen coordenadas.
 * Varias solicitudes pueden usar la misma coordenada dentro del mismo proyecto.
 * La unicidad aplica únicamente en ConstructionOrder, no aquí.
 *
 * ----------------------------------------------------------------------------
 *  ESTADO DE LA SOLICITUD
 * ----------------------------------------------------------------------------
 * • PENDING   → Registrada (estado inicial).
 * • APPROVED  → Materiales suficientes + coordenada libre (en órdenes).
 * • REJECTED  → Coordenada ocupada (existe orden) o stock insuficiente.
 * • FAILED    → Error inesperado en creación de orden.
 *
 * ----------------------------------------------------------------------------
 *  OBSERVACIONES
 * ----------------------------------------------------------------------------
 * Generadas automáticamente por el sistema. No editable.
 *
 * ----------------------------------------------------------------------------
 *  FECHA DE REGISTRO
 * ----------------------------------------------------------------------------
 * requestDate asignado en @PrePersist si no se envía desde el frontend.
 *
 * ----------------------------------------------------------------------------
 *  PROGRAMACIÓN FUNCIONAL
 * ----------------------------------------------------------------------------
 * @With permite crear copias inmutables para flujos funcionales/asincrónicos.
 *
 * ============================================================================
 */
@Entity
@Table(
        name = "construction_requests",
        indexes = {
                @Index(name = "idx_request_project", columnList = "project_id"),
                @Index(name = "idx_request_status", columnList = "request_status"),
                @Index(name = "idx_request_requested_by", columnList = "requested_by_user_id"),
                @Index(name = "idx_request_date", columnList = "request_date")
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
public class ConstructionRequest {

    /** Identificador único de la solicitud. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Proyecto al cual pertenece esta solicitud. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "project_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_request_project",
                    foreignKeyDefinition = "FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE RESTRICT"
            )
    )
    private Project project;

    /** Tipo de construcción solicitada (casa, edificio, lago, etc.). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "construction_type_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_request_construction_type",
                    foreignKeyDefinition = "FOREIGN KEY (construction_type_id) REFERENCES construction_types(id) ON DELETE RESTRICT"
            )
    )
    private ConstructionType constructionType;

    /** Latitud dentro del proyecto. */
    @Column(nullable = false)
    private Double latitude;

    /** Longitud dentro del proyecto. */
    @Column(nullable = false)
    private Double longitude;

    /** Usuario (arquitecto) que registró la solicitud. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "requested_by_user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_request_user",
                    foreignKeyDefinition = "FOREIGN KEY (requested_by_user_id) REFERENCES users(id) ON DELETE RESTRICT"
            )
    )
    private User requestedBy;

    /** Fecha en la que se registró la solicitud. */
    @Column(name = "request_date", nullable = false, updatable = false)
    private LocalDate requestDate;

    /** Estado actual de la solicitud. */
    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false, length = 20)
    @Builder.Default
    private RequestStatus requestStatus = RequestStatus.PENDING;

    /**
     * Observaciones generadas automáticamente:
     * - Razones de rechazo (coordenada ocupada, stock insuficiente)
     * - Detalle de materiales consumidos
     * - Mensaje de aprobación
     */
    @Column(name = "observations", length = 2000, updatable = false)
    private String observations;

    /** Asigna automáticamente la fecha si no viene del frontend. */
    @PrePersist
    public void prePersist() {
        requestDate = Optional.ofNullable(requestDate)
                .orElseGet(LocalDate::now);
    }
}
