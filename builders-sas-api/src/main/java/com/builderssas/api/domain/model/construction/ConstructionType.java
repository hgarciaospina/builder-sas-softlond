package com.builderssas.api.domain.model.construction;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a master definition for a specific type of construction.
 *
 * Examples include: House, Building, Bridge, Lake, Gym, etc.
 *
 * Design notes:
 * - Each construction type defines a typical duration and required materials.
 * - Unique constraint on the 'name' column prevents duplication.
 * - The relationship with ConstructionTypeMaterial is one-to-many but does NOT
 *   use cascade or orphan removal to avoid unintended deletions.
 * - Deletion of a ConstructionType is restricted if it is referenced by
 *   any ConstructionTypeMaterial, ConstructionOrder, or Snapshot.
 * - createdAt is used for audit purposes to know when the record was first created.
 */
@Entity
@Table(
        name = "construction_types",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_construction_types_name",
                        columnNames = "name"
                )
        },
        indexes = {
                @Index(name = "idx_construction_types_name", columnList = "name")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ConstructionType {

    /** Primary key — unique identifier for the construction type. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /**
     * Descriptive name of the construction type.
     * Example: "Residential House", "Industrial Building".
     */
    @Column(nullable = false, length = 150, unique = true)
    private String name;

    /**
     * Typical duration in days for this construction type.
     */
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    /**
     * Timestamp when this construction type was created.
     * Automatically set on first persist. Used for audit purposes.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    /**
     * Ensures createdAt is set before persisting the entity.
     */
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    /**
     * Materials required for this construction type.
     * Relationship is managed through ConstructionTypeMaterial.
     * Cascade operations are disabled to avoid unintended persistence or deletions.
     * Deletion of a ConstructionType is restricted at DB level.
     */
    @OneToMany(
            mappedBy = "constructionType",
            fetch = FetchType.LAZY
    )
    private Set<ConstructionTypeMaterial> materials = new HashSet<>();
}
