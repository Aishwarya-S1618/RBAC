package com.example.RBAC.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Entity to represent a JWT that has been revoked.
 * Useful for logout and token blacklisting scenarios.
 */
@Entity
@Table(name = "revoked_tokens", uniqueConstraints = @UniqueConstraint(columnNames = "token"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevokedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the revoked token entry

    @Column(nullable = false, unique = true, length = 512)
    private String token; // The JWT token string that has been revoked, must be unique

    @Column(nullable = false)
    private Long userId; // ID of the user associated with the revoked token

    @Column(nullable = false)
    private Instant revokedAt; // Timestamp indicating when the token was revoked (stored in UTC)

    /**
     * Factory method to create a revoked token with the current timestamp.
     * 
     * @param token The JWT token string to be revoked.
     * @return A new instance of RevokedToken.
     */
    public static RevokedToken of(String token) {
        return RevokedToken.builder()
                .token(token)
                .revokedAt(Instant.now())
                .build();
    }
}
