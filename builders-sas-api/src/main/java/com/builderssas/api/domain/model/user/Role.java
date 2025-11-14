package com.builderssas.api.domain.model.user;

import jakarta.persistence.*;
import lombok.*;

/**
 * Role entity - represents a system role (ADMIN, ARCHITECT, etc.)
 *
 * Design notes:
 * - Unique constraint on 'name' ensures no duplicate roles exist.
 * - Immutable by business logic: role names should rarely change.
 * - No cascade deletes; any entity referencing Role must handle deletion restrictions.
 */
@Entity
@Table(
        name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_roles_name", columnNames = "name")
        },
        indexes = {
                @Index(name = "idx_roles_name", columnList = "name")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

    /** Primary key — unique identifier for the role. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Role name (e.g., ADMIN, ARCHITECT). */
    @Column(nullable = false, length = 50)
    private String name;

    /** Optional description for the role. */
    @Column(length = 255)
    private String description;
}