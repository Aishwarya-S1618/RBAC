package com.example.RBAC.mapper;

import com.example.RBAC.dto.UserDto;
import com.example.RBAC.model.User;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * The UserMapper class is responsible for converting User entities to UserDto objects.
 * This is part of the mapping layer, which ensures separation of concerns between
 * the data model and the data transfer objects used in the application.
 */
public class UserMapper {

    /**
     * Converts a User entity to a UserDto object.
     *
     * @param user The User entity to be converted. Must not be null.
     * @return A UserDto object containing the user's ID, username, and roles.
     *         The roles are represented as a set of role names (strings).
     */
    public static UserDto toDTO(User user) {
        // Extract the role names from the User entity's roles and collect them into a set.
        Set<String> roles = user.getRoles()
                                .stream()
                                .map(role -> role.getName())
                                .collect(Collectors.toSet());

        // Create and return a new UserDto object with the extracted data.
        return new UserDto(user.getId(), user.getUsername(), roles);
    }

}

