package com.example.RBAC.repository;

import com.example.RBAC.model.Permission;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    // Find a permission by its name
    Optional<Permission> findByName(String name);

    // Check if a permission with the given name exists
    boolean existsByName(String name);

     // Find multiple permissions by their names
    Set<Permission> findByNameIn(Set<String> names);
}

