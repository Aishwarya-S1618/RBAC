package com.example.RBAC.controller;

import com.example.RBAC.service.AdminService;
import com.example.RBAC.service.RoleService;
import com.example.RBAC.service.PermissionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.example.RBAC.dto.UserDto;
import com.example.RBAC.dto.RoleDto;
import com.example.RBAC.dto.RoleUpdateRequest;
import com.example.RBAC.mapper.UserMapper;
import com.example.RBAC.repository.UserRepository;
import com.example.RBAC.model.Permission;

import com.example.RBAC.dto.PermissionRequest;
import com.example.RBAC.dto.RolePermissionRequest;
import com.example.RBAC.dto.RoleRequest;
import com.example.RBAC.dto.PermissionUpdateRequest;
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

    // Service and repository dependencies injected via constructor
    private final AdminService adminService;
    private final RoleService roleService;
    private final PermissionService permissionService;

    private final UserRepository userRepository;

    // Retrieve all users in the system
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> userDTOs = userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    // Retrieve a specific user by their ID
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto userDTO = adminService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    // Delete a user by their ID
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    // Update roles assigned to a specific user
    @PutMapping("/users/{userId}/roles")
    public ResponseEntity<UserDto> updateUserRoles(
            @PathVariable Long userId,
            @Valid @RequestBody RoleUpdateRequest request
    ) {
        return ResponseEntity.ok(roleService.updateUserRoles(userId, request));
    }

    // Revoke specific roles from a user
    @DeleteMapping("/users/{userId}/roles")
    public ResponseEntity<UserDto> revokeRolesFromUser(
            @PathVariable Long userId,
            @RequestBody RoleUpdateRequest request
    ) {
        // Call service to revoke roles and return updated user details
        UserDto updatedUser = roleService.revokeRolesFromUser(userId, request.getRoles());
        return ResponseEntity.ok(updatedUser);
    }

    // Retrieve all roles available in the system
    @GetMapping("/roles")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        // Call service to fetch all roles
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    // Add a new role to the system
    @PostMapping("/roles")
    public ResponseEntity<RoleDto> addRole(@RequestBody RoleRequest request) {
        RoleDto roleDto = roleService.createRole(request);
        return ResponseEntity.ok(roleDto);
    }

    // Add a new permission to the system
    @PostMapping("/permissions")
    public ResponseEntity<Permission> addPermission(@RequestBody PermissionRequest request) {
        return ResponseEntity.ok(permissionService.addPermission(request.getPermission()));
    }

    // Assign permissions to a specific role
    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<String> assignPermissionsToRole(
            @PathVariable Long roleId,
            @RequestBody RolePermissionRequest request
    ) {
        permissionService.assignPermissionsToRole(roleId, request.getPermissions());
        return ResponseEntity.ok("Permissions assigned successfully");
    }

    // Revoke permissions from a specific role
    @DeleteMapping("/roles/{roleId}/permissions")
    public ResponseEntity<String> revokePermissionsFromRole(
            @PathVariable Long roleId,
            @RequestBody RolePermissionRequest request
    ) {
        permissionService.revokePermissionsFromRole(roleId, request.getPermissions());
        return ResponseEntity.ok("Permissions revoked successfully");
    }

    // Retrieve all permissions assigned to a specific role
    @GetMapping("/roles/{roleId}/permissions")
    public ResponseEntity<Set<String>> getPermissionsForRole(@PathVariable Long roleId) {
        Set<String> permissions = permissionService.getPermissionsForRole(roleId);
        return ResponseEntity.ok(permissions);
    }

    // List all permissions available in the system
    @GetMapping("/permissions")
    public ResponseEntity<List<Permission>> listPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    // Delete a specific permission by its ID
    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<String> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.ok("Permission deleted");
    }

    // Update permissions assigned to a specific role
    @PutMapping("/roles/{roleId}/permissions")
    public ResponseEntity<RoleDto> updateRolePermissions(
            @PathVariable Long roleId,
            @Valid @RequestBody PermissionUpdateRequest request
    ) {
        return ResponseEntity.ok(permissionService.updateRolePermissions(roleId, request));
    }

}

