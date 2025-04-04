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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                       UserRepository userRepository, PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository, RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
            HashSet<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
        }

        userRepository.save(user);
        UserDetails userDetails = new CustomUserDetails(user);
        return jwtUtil.generateToken(userDetails);
    }

    public Map<String, String> authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        UserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = createRefreshToken(user);
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    public String createRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user); // delete existing token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(604800)); // 7 days
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public Map<String, String> refreshAccessToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.deleteByToken(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        User user = token.getUser();
        UserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtUtil.generateToken(userDetails);
        return Map.of("accessToken", newAccessToken);
    }


}
