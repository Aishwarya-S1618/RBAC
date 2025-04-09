package com.example.RBAC.controller;

import java.util.Map;

import com.example.RBAC.model.User;
import com.example.RBAC.repository.UserRepository;
import com.example.RBAC.security.CustomUserDetails;
import com.example.RBAC.security.JwtUtil;
import com.example.RBAC.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

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
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return ResponseEntity.ok(authService.refreshAccessToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok("User logged out successfully");
    }

}
