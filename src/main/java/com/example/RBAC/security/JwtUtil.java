package com.example.RBAC.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for handling JWT operations such as generation, validation, and extraction.
 */
@Component
public class JwtUtil {

    private final Key signingKey;
    private final long jwtExpiration;

    /**
     * Constructor to initialize signing key and expiration time.
     * @param secret Secret key for signing JWT.
     * @param jwtExpiration Expiration time for JWT in milliseconds.
     */
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long jwtExpiration) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpiration = jwtExpiration;
    }

    /**
     * Generates a JWT for the given user details.
     * @param userDetails User details containing username and roles.
     * @return Generated JWT as a string.
     */
    public String generateToken(UserDetails userDetails) {
        String username = userDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        List<String> roles = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        System.out.println(" Generating JWT for User: " + username);
        System.out.println(" Roles assigned in JWT: " + roles);
        System.out.println(" JWT Expiration Time: " + jwtExpiration + " milliseconds");
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts all claims from the given JWT.
     * @param token JWT token.
     * @return Claims contained in the JWT.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts a specific claim from the given JWT using the provided claims resolver function.
     * @param token JWT token.
     * @param claimsResolver Function to resolve the claim from the claims.
     * @param <T> Type of the claim.
     * @return Extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts the username (subject) from the given JWT.
     * @param token JWT token.
     * @return Username contained in the JWT.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Validates the given JWT.
     * @param token JWT token.
     * @return True if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            System.err.println("Invalid JWT: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extracts the roles from the given JWT.
     * @param token JWT token.
     * @return List of roles contained in the JWT.
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

}
