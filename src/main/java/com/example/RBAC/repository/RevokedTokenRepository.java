package com.example.RBAC.repository;
import com.example.RBAC.model.RevokedToken;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {

    // Check if a token exists in the revoked tokens table
    boolean existsByToken(String token);

    // Delete a specific token from the revoked tokens table
    void deleteByToken(String token);

    // Find a specific token in the revoked tokens table
    Optional<RevokedToken> findByToken(String token);

    // Delete all tokens associated with a specific user ID
    void deleteAllByUserId(Long userId);

}

