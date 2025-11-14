package com.builderssas.api.domain.model.construction;

import com.builderssas.api.domain.model.material.MaterialType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing the association between a ConstructionType and the materials required for it.
 *
 * This table defines how much of each material is needed for a specific type of construction.
 *
 * Design notes:
 * - Each record links one ConstructionType with one MaterialType.
 * - Uniqueness constraint ensures no duplicate pairs of (construction_type, material_type).
 * - Both relationships are mandatory (optional = false).
 * - No cascade operations are applied to avoid unintentional deletions of master data.
 * - Deletions are restricted at the database level via ON DELETE RESTRICT semantics.
 */

@Entity
@Table(
        name = "construction_type_materials",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_ctm_construction_material",
                        columnNames = {"construction_type_id", "material_type_id"}
                )
        },
        indexes = {
                @Index(name = "idx_ctm_construction_type", columnList = "construction_type_id"),
                @Index(name = "idx_ctm_material_type", columnList = "material_type_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ConstructionTypeMaterial {

    /** Primary key — unique identifier for this ConstructionTypeMaterial entry. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /**
     * Reference to the associated ConstructionType.
     * Mandatory. Each material is linked to exactly one construction type.
     * No cascade operations are enabled. Deletion restricted at DB level.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "construction_type_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_ctm_construction_type",
                    foreignKeyDefinition = "FOREIGN KEY (construction_type_id) REFERENCES construction_types(id) ON DELETE RESTRICT"
            )
    )
    private ConstructionType constructionType;

    /**
     * Reference to the MaterialType required by this ConstructionType.
     * Mandatory. Prevents orphan material definitions.
     * No cascade operations are enabled. Deletion restricted at DB level.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "material_type_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_ctm_material_type",
                    foreignKeyDefinition = "FOREIGN KEY (material_type_id) REFERENCES material_types(id) ON DELETE RESTRICT"
            )
    )
    private MaterialType materialType;

    /**
     * Quantity of the material required for this construction type.
     */
    @Column(name = "quantity_required", nullable = false)
    private Double quantityRequired = 1.0;
}