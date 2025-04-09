package com.example.RBAC.service;

import com.example.RBAC.model.Permission;
import com.example.RBAC.model.Role;
import com.example.RBAC.model.User;
import com.example.RBAC.dto.RoleDto;
import com.example.RBAC.dto.RoleUpdateRequest;
import com.example.RBAC.dto.UserDto;
import com.example.RBAC.repository.RoleRepository;
import com.example.RBAC.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import com.example.RBAC.repository.RevokedTokenRepository;
import com.example.RBAC.repository.RefreshTokenRepository;
import com.example.RBAC.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        // Fetch and return all users from the database
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        // Fetch a user by ID and map it to a UserDto, or throw an exception if not found
        return userRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void deleteUserById(Long userId) {
        // Delete a user by ID along with their associated tokens
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Delete refresh token(s)
        refreshTokenRepository.deleteByUser(user);

        // 2. Delete revoked tokens manually (safe cleanup)
        revokedTokenRepository.deleteAllByUserId(userId);

        // 3. Finally delete the user
        userRepository.delete(user);

        System.out.println("Successfully deleted user and related tokens.");
    }

    @Transactional
    public UserDto updateUserRoles(Long userId, RoleUpdateRequest request) {
        // Update the roles of a user based on the provided RoleUpdateRequest
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<Role> roles = roleRepository.findByNameIn(request.getRoles());
        if (roles.size() != request.getRoles().size()) {
            throw new IllegalArgumentException("One or more roles are invalid");
        }

        user.setRoles(new HashSet<>(roles));
        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles() {
        // Fetch and return all roles as a list of RoleDto objects
        return roleRepository.findAll().stream()
                .map(role -> {
                    RoleDto dto = new RoleDto();
                    dto.setId(role.getId());
                    dto.setName(role.getName());
                    return dto;
                }).collect(Collectors.toList());
    }

    @Transactional
    public void createRole(String roleName) {
        // Create a new role with the given name, or throw an exception if it already exists
        if (roleRepository.findByName(roleName).isPresent()) {
            throw new RuntimeException("Role already exists");
        }
        Role role = new Role();
        role.setName(roleName);
        roleRepository.save(role);
    }

    @Transactional
    public UserDto revokeRolesFromUser(Long userId, Set<String> roleNames) {
        // Revoke specific roles from a user and return the updated user as a UserDto
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<Role> updatedRoles = user.getRoles().stream()
                .filter(role -> !roleNames.contains(role.getName()))
                .collect(Collectors.toSet());

        user.setRoles(updatedRoles);
        User updated = userRepository.save(user);
        return mapToDTO(updated);
    }

    private UserDto mapToDTO(User user) {
        // Map a User entity to a UserDto object
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        return dto;
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

}

