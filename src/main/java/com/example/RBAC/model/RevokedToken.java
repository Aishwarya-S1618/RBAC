package com.example.RBAC.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Entity to represent a JWT that has been revoked.
 * This is useful for logout and token blacklisting scenarios.
 */
@Entity
@Table(name = "revoked_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevokedToken {

    /**
     * Primary key for the revoked token entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The JWT token string that has been revoked.
     * This is expected to be unique.
     */
    @Column(nullable = false, unique = true, length = 512)
    private String token;

    /**
     * Timestamp indicating when the token was revoked.
     * Stored as UTC.
     */
    @Column(nullable = false)
    private Instant revokedAt;

    /**
     * Optional: Convenience factory method for revoking a token now.
     */
    public static RevokedToken of(String token) {
        return RevokedToken.builder()
                .token(token)
                .revokedAt(Instant.now())
                .build();
    }
}
