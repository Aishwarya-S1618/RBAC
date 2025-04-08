package com.example.RBAC.repository;
import com.example.RBAC.model.RevokedToken;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    boolean existsByToken(String token);
    void deleteByToken(String token);
    Optional<RevokedToken> findByToken(String token);

}

