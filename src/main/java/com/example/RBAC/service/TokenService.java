package com.example.RBAC.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.example.RBAC.model.RevokedToken;
import com.example.RBAC.repository.RevokedTokenRepository;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RevokedTokenRepository revokedTokenRepository;

    public void revokeToken(String token) {
        RevokedToken revoked = new RevokedToken();
        revoked.setToken(token);
        revoked.setRevokedAt(Instant.now());
        revokedTokenRepository.save(revoked);
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }
}

