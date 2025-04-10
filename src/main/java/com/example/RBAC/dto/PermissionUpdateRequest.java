package com.example.RBAC.dto;

import lombok.Data;
import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for handling permission update requests.
 */
@Data
public class PermissionUpdateRequest {
    @NotNull(message = "Permissions list cannot be null")
    @NotEmpty(message = "Permissions list cannot be empty")
    private Set<String> permissions;
}