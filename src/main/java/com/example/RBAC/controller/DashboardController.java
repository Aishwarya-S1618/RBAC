package com.example.RBAC.controller;

import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.RBAC.security.JwtUtil;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final JwtUtil jwtUtil;

    public DashboardController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> userDashboard(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(Map.of(
        "message", "Welcome to the USER dashboard!",
        "username", username
    ));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminDashboard() {
        return ResponseEntity.ok("Welcome to the ADMIN dashboard!");
    }

    @GetMapping("/roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getRoles(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer "
        List<String> roles = jwtUtil.extractRoles(token);
        return ResponseEntity.ok(roles);
}
}

