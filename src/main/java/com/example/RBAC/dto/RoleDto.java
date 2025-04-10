package com.example.RBAC.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for representing role data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private Long id;
    private String name;
    private Set<String> permissions;
}
