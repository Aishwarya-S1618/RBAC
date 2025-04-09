package com.example.RBAC.dto;

import java.util.Set;

import lombok.Data;

@Data
public class RolePermissionRequest {
    private Set<String> permissions;
}