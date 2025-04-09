package com.example.RBAC.repository;


import com.example.RBAC.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // Find a role by its name
    Optional<Role> findByName(String name);

    // Find multiple roles by their names
    Set<Role> findByNameIn(Set<String> names);
}
