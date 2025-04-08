package com.example.RBAC.controller;

import com.example.RBAC.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.example.RBAC.dto.UserDto;
import com.example.RBAC.dto.RoleDto;
import com.example.RBAC.dto.RoleUpdateRequest;
import com.example.RBAC.mapper.UserMapper;
import com.example.RBAC.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor


public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> userDTOs = userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/users/{id}")
        public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
            UserDto userDTO = adminService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{userId}/roles")
    public ResponseEntity<UserDto> updateUserRoles( @PathVariable Long userId,
        @Valid @RequestBody RoleUpdateRequest request
    ) {
        return ResponseEntity.ok(adminService.updateUserRoles(userId, request));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(adminService.getAllRoles());
    }

    // @PostMapping("/create-role")
    // @PreAuthorize("hasRole('ADMIN')")
    // public ResponseEntity<String> createRole(@RequestParam String roleName) {
    //     adminService.createRole(roleName);
    //     return ResponseEntity.ok("Role created successfully");
    // }
}

