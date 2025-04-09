package com.example.RBAC.controller;

import com.example.RBAC.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.example.RBAC.dto.UserDto;
import com.example.RBAC.dto.RoleDto;
import com.example.RBAC.dto.RoleUpdateRequest;
import com.example.RBAC.mapper.UserMapper;
import com.example.RBAC.repository.UserRepository;
import com.example.RBAC.model.Permission;
import com.example.RBAC.service.PermissionService;
import com.example.RBAC.dto.PermissionRequest;
import com.example.RBAC.dto.RolePermissionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor


public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final PermissionService permissionService;

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

    @DeleteMapping("/users/{userId}/roles")
    public ResponseEntity<UserDto> revokeRolesFromUser(
            @PathVariable Long userId,
            @RequestBody RoleUpdateRequest request
    ) {
        UserDto updatedUser = adminService.revokeRolesFromUser(userId, request.getRoles());
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(adminService.getAllRoles());
    }

    @PostMapping("/permissions")
    public ResponseEntity<Permission> addPermission(@RequestBody PermissionRequest request) {
        return ResponseEntity.ok(permissionService.addPermission(request.getPermission()));
    }

    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<String> assignPermissionsToRole(
            @PathVariable Long roleId,
            @RequestBody RolePermissionRequest request
    ) {
        permissionService.assignPermissionsToRole(roleId, request.getPermissions());
        return ResponseEntity.ok("Permissions assigned successfully");
    }

    @DeleteMapping("/roles/{roleId}/permissions")
    public ResponseEntity<String> revokePermissionsFromRole(
            @PathVariable Long roleId,
            @RequestBody RolePermissionRequest request
    ) {
        permissionService.revokePermissionsFromRole(roleId, request.getPermissions());
        return ResponseEntity.ok("Permissions revoked successfully");
    }

    @GetMapping("/roles/{roleId}/permissions")
    public ResponseEntity<Set<String>> getPermissionsForRole(@PathVariable Long roleId) {
        Set<String> permissions = adminService.getPermissionsForRole(roleId);
        return ResponseEntity.ok(permissions);
    }
    @GetMapping("/permissions")
    public ResponseEntity<List<Permission>> listPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<String> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.ok("Permission deleted");
    }

}

