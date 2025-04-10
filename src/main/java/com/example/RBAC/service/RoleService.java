package com.example.RBAC.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.RBAC.dto.RoleDto;
import com.example.RBAC.dto.RoleRequest;
import com.example.RBAC.dto.RoleUpdateRequest;
import com.example.RBAC.dto.UserDto;
import com.example.RBAC.exception.ResourceNotFoundException;
import com.example.RBAC.model.Permission;
import com.example.RBAC.model.Role;
import com.example.RBAC.model.User;
import com.example.RBAC.repository.RoleRepository;
import com.example.RBAC.repository.UserRepository;
import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class RoleService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

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
                    dto.setPermissions(role.getPermissions()
                        .stream()
                        .map(Permission::getName)
                        .collect(Collectors.toSet()));
                    return dto;
                }).collect(Collectors.toList());
    }

    @Transactional
    public RoleDto createRole(RoleRequest request) {
        // Create a new role with the given name, or throw an exception if it already exists
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Role already exists");
        }
        Role role = new Role();
        role.setName(request.getName());
        roleRepository.save(role);
        return mapToDTO(role);
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

        private RoleDto mapToDTO(Role role) {
        // Map a User entity to a UserDto object
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setPermissions(role.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet()));
        return dto;
    }
}
