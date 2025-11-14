package com.builderssas.api.domain.model.material;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa el catálogo de materiales disponibles.
 *
 * Como no se manejan reservas ni histórico, el stock disponible
 * se almacena directamente en esta entidad.
 */
@Entity
@Table(
        name = "material_types",
        uniqueConstraints = @UniqueConstraint(name = "uq_material_types_code", columnNames = "code"),
        indexes = {
                @Index(name = "idx_material_types_code", columnList = "code"),
                @Index(name = "idx_material_types_name", columnList = "name")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MaterialType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código corto: Ce, Gr, Ar, Ma, Ad */
    @Column(nullable = false, length = 8)
    private String code;

    /** Nombre descriptivo: Cemento, Grava, Arena, Madera, Adobe */
    @Column(nullable = false, length = 80)
    private String name;

    /** Unidad de medida (kg, m3, unid, etc.) */
    @Column(nullable = false, length = 16)
    private String unit;

    /** Stock disponible actual del material */
    @Column(nullable = false)
    private Double stock;
}
