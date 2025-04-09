package com.example.RBAC.dto;

import lombok.Data;
import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for handling role update requests.
 */
@Data
public class RoleUpdateRequest {
    @NotNull(message = "Roles list cannot be null")
    @NotEmpty(message = "Roles list cannot be empty")
    private Set<String> roles;
}

