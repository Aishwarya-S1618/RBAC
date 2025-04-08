package com.example.RBAC.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import com.example.RBAC.model.RevokedToken;
import com.example.RBAC.repository.RevokedTokenRepository;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RevokedTokenRepository revokedTokenRepository;

    @Transactional
    public void revokeToken(String token) {
        if (!revokedTokenRepository.existsByToken(token)){
        RevokedToken revoked = new RevokedToken();
        revoked.setToken(token);
        revoked.setRevokedAt(Instant.now());
        revokedTokenRepository.save(revoked);
        } else {
            System.out.println("Token already revoked.");
        }
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }

    @Transactional
    public void removeToken(String token) {
        Optional<RevokedToken> existing = revokedTokenRepository.findByToken(token);
        existing.ifPresent(Rtoken -> revokedTokenRepository.deleteById(Rtoken.getId()));
    }
}