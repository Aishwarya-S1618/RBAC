package com.example.RBAC.controller;

import java.util.Map;

import com.example.RBAC.model.User;
import com.example.RBAC.repository.UserRepository;
import com.example.RBAC.security.CustomUserDetails;
import com.example.RBAC.security.JwtUtil;
import com.example.RBAC.service.AuthService;
import com.example.RBAC.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TokenService tokenService; 

    public AuthController(AuthService authService, JwtUtil jwtUtil, UserRepository userRepository, TokenService tokenService) {
        this.tokenService = tokenService;
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @GetMapping("/secure-endpoint")
    public ResponseEntity<Map<String, String>> secureEndpoint() {
        return ResponseEntity.ok(Map.of("message", "This is a secure endpoint"));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        System.out.println("Register API called with user: " + user.getUsername());
        String token = authService.register(user);
        return ResponseEntity.ok(Map.of("token", token, "message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        System.out.println("Login API called with user: " + username);
        Map<String,String> tokens = authService.authenticate(username, password);
        return ResponseEntity.ok(Map.of(
            "AccessToken", tokens.get("accessToken"),
            "RefreshToken", tokens.get("refreshToken")

        ));

    }


    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid refresh token"));
        }

        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        tokenService.revokeToken(token); // Blacklist or invalidate refresh token

        return ResponseEntity.ok("Logout successful");
    }

}
