package com.example.RBAC.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Entity to represent a refresh token used for session management.
 * Refresh tokens allow users to obtain new access tokens without re-authenticating.
 */
@Entity
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the refresh token entry

    @Column(unique = true)
    private String token; // The refresh token string, must be unique

    @OneToOne
    private User user; // The user associated with this refresh token

    private Instant expiryDate; // The expiration date and time of the refresh token
}
