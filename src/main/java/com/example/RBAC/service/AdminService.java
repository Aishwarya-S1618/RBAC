package com.example.RBAC.service;

import com.example.RBAC.model.Role;
import com.example.RBAC.model.User;
import com.example.RBAC.dto.UserDto;
import com.example.RBAC.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import com.example.RBAC.repository.RevokedTokenRepository;
import com.example.RBAC.repository.RefreshTokenRepository;
import com.example.RBAC.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public void deleteUserById(Long userId) {
        // Delete a user by ID along with their associated tokens
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 1. Delete refresh token(s)
        refreshTokenRepository.deleteByUser(user);

        // 2. Delete revoked tokens manually (safe cleanup)
        revokedTokenRepository.deleteAllByUserId(userId);

        // 3. Finally delete the user
        userRepository.delete(user);

        System.out.println("Successfully deleted user and related tokens.");
    }

    private UserDto mapToDTO(User user) {
        // Map a User entity to a UserDto object
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        return dto;
    }

}

