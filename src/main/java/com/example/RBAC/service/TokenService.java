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

    /**
     * Revokes a token by saving it in the revoked token repository.
     * If the token is already revoked, logs a message instead.
     *
     * @param token the token to be revoked
     */
    @Transactional
    public void revokeToken(String token) {
        if (!revokedTokenRepository.existsByToken(token)) {
            RevokedToken revoked = new RevokedToken();
            revoked.setToken(token);
            revoked.setRevokedAt(Instant.now());
            revokedTokenRepository.save(revoked);
        } else {
            System.out.println("Token already revoked.");
        }
    }

    /**
     * Checks if a token is revoked by querying the revoked token repository.
     *
     * @param token the token to check
     * @return true if the token is revoked, false otherwise
     */
    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }

    /**
     * Removes a token from the revoked token repository if it exists.
     *
     * @param token the token to be removed
     */
    @Transactional
    public void removeToken(String token) {
        Optional<RevokedToken> existing = revokedTokenRepository.findByToken(token);
        existing.ifPresent(Rtoken -> revokedTokenRepository.deleteById(Rtoken.getId()));
    }
}