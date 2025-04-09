package com.example.RBAC.dto;

import java.util.Set;

import lombok.Data;

/**
 * DTO for handling role-permission mapping requests.
 */
@Data
public class RolePermissionRequest {
    private Set<String> permissions;
}