package com.example.RBAC.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a permission in the system.
 * This class is mapped to the "permissions" table in the database.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permissions")
public class Permission {

    /**
     * Primary key for the Permission entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique name of the permission.
     */
    @Column(nullable = false, unique = true)
    private String name;
}

