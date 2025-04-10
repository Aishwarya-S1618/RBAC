package com.example.RBAC.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.RBAC.dto.PermissionUpdateRequest;
import com.example.RBAC.dto.RoleDto;
import com.example.RBAC.exception.ResourceNotFoundException;
import com.example.RBAC.model.Permission;
import com.example.RBAC.model.Role;
import com.example.RBAC.repository.RoleRepository;
import com.example.RBAC.repository.PermissionRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Set<String> getPermissionsForRole(Long roleId) {
        // Fetch and return all permissions associated with a specific role
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        return role.getPermissions()
                .stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }

    public Permission addPermission(String name) {
        if (permissionRepository.existsByName(name)) {
            throw new IllegalArgumentException("Permission already exists");
        }

        Permission permission = new Permission();
        permission.setName(name);
        return permissionRepository.save(permission);
    }

    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Permission not found");
        }
        permissionRepository.deleteById(id);

    }

    @Transactional
    public RoleDto assignPermissionsToRole(Long roleId, Set<String> permissionNames) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        Set<Permission> permissions = permissionNames.stream()
                .map(name -> permissionRepository.findByName(name)
                        .orElseThrow(() -> new ResourceNotFoundException("Permission not found: " + name)))
                .collect(Collectors.toSet());

        role.getPermissions().addAll(permissions);
        roleRepository.save(role);
        return mapToDTO(role);
    }

    @Transactional
    public RoleDto revokePermissionsFromRole(Long roleId, Set<String> permissionNames) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        role.getPermissions().removeIf(permission ->
            permissionNames.contains(permission.getName())
        );

        roleRepository.save(role);
        return mapToDTO(role);
    }

    @Transactional
    public RoleDto updateRolePermissions(Long roleId, PermissionUpdateRequest request) {
        // Update the permissions of a role based on the provided PermissionUpdateRequest
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        Set<Permission> permissions = new HashSet<>(permissionRepository.findByNameIn(request.getPermissions()));
        if (permissions.size() != request.getPermissions().size()) {
            throw new IllegalArgumentException("One or more Permissions are invalid");
        }

        role.setPermissions(permissions);
        Role savedRole = roleRepository.save(role);
        return mapToDTO(savedRole);
    }

    private RoleDto mapToDTO(Role role) {
        // Map a User entity to a UserDto object
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setPermissions(role.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet()));
        return dto;
    }

}

