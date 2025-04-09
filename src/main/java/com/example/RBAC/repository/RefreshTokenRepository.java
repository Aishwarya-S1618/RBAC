package com.example.RBAC.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RBAC.model.RefreshToken;
import com.example.RBAC.model.User; // ✅ Add this import

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Find a refresh token by its token string
    Optional<RefreshToken> findByToken(String token);

    // Delete a refresh token by its token string
    void deleteByToken(String token);

    // Delete a refresh token associated with a specific user
    void deleteByUser(User user);
}
