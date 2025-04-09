package com.example.RBAC.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.RBAC.exception.ResourceNotFoundException;
import com.example.RBAC.model.Permission;
import com.example.RBAC.model.Role;
import com.example.RBAC.repository.RoleRepository;
import com.example.RBAC.repository.PermissionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
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
    public void assignPermissionsToRole(Long roleId, Set<String> permissionNames) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        Set<Permission> permissions = permissionNames.stream()
                .map(name -> permissionRepository.findByName(name)
                        .orElseThrow(() -> new ResourceNotFoundException("Permission not found: " + name)))
                .collect(Collectors.toSet());

        role.getPermissions().addAll(permissions);
        roleRepository.save(role);
    }

    @Transactional
    public void revokePermissionsFromRole(Long roleId, Set<String> permissionNames) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        role.getPermissions().removeIf(permission ->
            permissionNames.contains(permission.getName())
        );

        roleRepository.save(role);
    }

}

