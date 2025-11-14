package com.builderssas.api.domain.model.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

/**
 * UserRole entity — explicit join table between User and Role.
 *
 * Design notes:
 * - Each user can have multiple roles, but a user-role combination must be unique.
 * - No cascade deletes: deleting a user or role will not delete the other entity.
 * - Foreign keys enforce referential integrity using ON DELETE RESTRICT.
 * - Automatically sets the system timestamp when inserted.
 * - Indexed for fast lookup by user or role.
 */
@Entity
@Table(
        name = "user_roles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_user_roles_user_role",
                        columnNames = {"user_id", "role_id"}
                )
        },
        indexes = {
                @Index(name = "idx_user_roles_user", columnList = "user_id"),
                @Index(name = "idx_user_roles_role", columnList = "role_id")
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
public class UserRole {

    /** Primary key — unique identifier for the record. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** User associated with this role. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_user_roles_user",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT"
            )
    )
    private User user;

    /** Role assigned to the user. */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "role_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_user_roles_role",
                    foreignKeyDefinition = "FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT"
            )
    )
    private Role role;

    /** Automatically set when the role is assigned to a user. */
    @Column(
            name = "assigned_at",
            nullable = false,
            updatable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP"
    )
    private OffsetDateTime assignedAt;

    /**
     * Automatically assigns the current system timestamp
     * when the entity is first persisted.
     */
    @PrePersist
    protected void onCreate() {
        if (assignedAt == null) {
            assignedAt = OffsetDateTime.now();
        }
    }
}
