package com.builderssas.api.domain.model.user;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User entity - system users (architects, admins, etc.)
 *
 * Design notes:
 * - Unique constraints ensure no duplicate email, username, or firstname+lastname combination exists.
 * - User roles are managed via a join table with unique constraints and ON DELETE RESTRICT.
 * - Builder pattern allows flexible creation while maintaining immutability of sets externally.
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uq_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uq_users_fullname", columnNames = {"firstname", "lastname"})
        },
        indexes = {
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_username", columnList = "username"),
                @Index(name = "idx_users_fullname", columnList = "firstname, lastname")
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
public class User {

    /** Primary key — unique identifier for the user. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Username, unique per user. */
    @Column(nullable = false, length = 100, unique = true)
    private String username;

    /** First name of the user. */
    @Column(nullable = false, length = 100)
    private String firstname;

    /** Last name of the user. */
    @Column(nullable = false, length = 100)
    private String lastname;

    /** Email address, unique per user. */
    @Column(nullable = false, length = 255, unique = true)
    private String email;

    /** Password hash. Optional if using external auth. */
    @Column(length = 255)
    private String password;

    /** Whether the user account is active. */
    @Builder.Default
    private Boolean active = Boolean.TRUE;

    /**
     * Roles assigned to the user.
     * - Managed via the user_roles join table through the UserRole entity.
     * - No cascade deletes: deleting a user or role will not delete the other entity.
     * - Unique constraint prevents duplicate role assignments per user.
     *
     * ✅ CORRECCIÓN:
     * Se reemplaza el @ManyToMany por un @OneToMany hacia UserRole,
     * evitando conflicto con la entidad UserRole explícita.
     */
    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.EAGER,
            orphanRemoval = false
    )
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();
}
