package com.example.RBAC.service;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import com.example.RBAC.model.User;
import com.example.RBAC.exception.UserAlreadyExistsException;
import com.example.RBAC.model.RefreshToken;
import com.example.RBAC.model.Role;
import com.example.RBAC.repository.UserRepository;
import com.example.RBAC.repository.RefreshTokenRepository;
import com.example.RBAC.repository.RoleRepository;
import com.example.RBAC.security.CustomUserDetails;
import com.example.RBAC.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    // Dependencies injected via constructor
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;

    // Constructor for dependency injection
    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                       UserRepository userRepository, PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository, RefreshTokenRepository refreshTokenRepository, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenService = tokenService;
    }

    // Registers a new user and returns a JWT access token
    public String register(User user) {
        // Check if the user already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        // Encode the user's password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign default role if no roles are provided
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
            HashSet<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
        }

        // Save the user and generate a JWT token
        userRepository.save(user);
        UserDetails userDetails = new CustomUserDetails(user);
        return jwtUtil.generateToken(userDetails);
    }

    @Transactional
    public Map<String, String> authenticate(String username, String password) {
        // Authenticate the user
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // Retrieve the user from the database
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate access and refresh tokens
        UserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtUtil.generateToken(userDetails);

        // Revoke any previously revoked tokens
        if (tokenService.isTokenRevoked(accessToken)) {
            tokenService.removeToken(accessToken);
        }

        String refreshToken = createRefreshToken(user);
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    @Transactional
    public String createRefreshToken(User user) {
        // Delete any existing refresh tokens for the user
        refreshTokenRepository.deleteByUser(user);

        // Create a new refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(604800)); // 7 days
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public Map<String, String> refreshAccessToken(String refreshToken) {
        // Validate the refresh token
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        // Check if the token has expired
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.deleteByToken(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        // Generate a new access token
        User user = token.getUser();
        UserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtUtil.generateToken(userDetails);
        return Map.of("accessToken", newAccessToken);
    }

    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        // Find and delete the refresh token
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        refreshTokenRepository.delete(token);
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        // Extract the token from the Authorization header
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            // Revoke the access token
            tokenService.revokeToken(token);

            // Delete the user's refresh token
            String username = jwtUtil.extractUsername(token);
            System.out.println("🧹 Deleting refresh token for user: " + username);
            userRepository.findByUsername(username).ifPresent(user -> {
                refreshTokenRepository.deleteByUser(user);
                user.setLastAccessToken(null);
                userRepository.save(user);
            });
        } else {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
    }
}
