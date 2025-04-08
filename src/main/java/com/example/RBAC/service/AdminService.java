package com.example.RBAC.service;

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


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    @Transactional
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Delete refresh token(s)
        refreshTokenRepository.deleteByUser(user);

        // 2. Delete revoked tokens manually (optional but safe cleanup)
        revokedTokenRepository.deleteAllByUserId(userId); // You must add this method to repo

        // 3. Finally delete the user
        userRepository.delete(user);

        System.out.println("Successfully deleted user and related tokens.");
    }

    @Transactional
    public UserDto updateUserRoles(Long userId, RoleUpdateRequest request) {
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

    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> {
                    RoleDto dto = new RoleDto();
                    dto.setId(role.getId());
                    dto.setName(role.getName());
                    return dto;
                }).collect(Collectors.toList());
    }


    public void createRole(String roleName) {
        if (roleRepository.findByName(roleName).isPresent()) {
            throw new RuntimeException("Role already exists");
        }
        Role role = new Role();
        role.setName(roleName);
        roleRepository.save(role);
    }
    private UserDto mapToDTO(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        return dto;
    }
}

