package com.example.RBAC.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a user in the system.
 * This class is mapped to the "users" table in the database.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    /**
     * Primary key for the User entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username for the user.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Encrypted password for the user.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Stores the last access token issued to the user.
     */
    @Column(columnDefinition = "TEXT")
    private String lastAccessToken;

    /**
     * Roles assigned to the user.
     * This is a many-to-many relationship with the Role entity.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}

