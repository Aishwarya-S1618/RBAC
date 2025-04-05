package com.example.RBAC.config;

import com.example.RBAC.model.Permission;
import com.example.RBAC.model.Role;
import com.example.RBAC.repository.PermissionRepository;
import com.example.RBAC.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public DataInitializer(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public void run(String... args) {
        // Check if roles already exist to avoid duplication
        if (roleRepository.count() == 0 && permissionRepository.count() == 0) {
            Permission readUser = new Permission(null, "READ_USER");
            Permission writeUser = new Permission(null, "WRITE_USER");
            permissionRepository.save(readUser);
            permissionRepository.save(writeUser);

            Role adminRole = new Role(null, "ADMIN", new HashSet<>(), Set.of(readUser, writeUser));
            Role userRole = new Role(null, "USER", new HashSet<>(), Set.of(readUser));

            roleRepository.save(adminRole);
            roleRepository.save(userRole);

            System.out.println("Initialized roles and permissions.");
        }
    }
}

