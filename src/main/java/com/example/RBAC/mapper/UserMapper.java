package com.example.RBAC.mapper;

import com.example.RBAC.dto.UserDto;
import com.example.RBAC.model.User;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto toDTO(User user) {
        Set<String> roles = user.getRoles()
                                .stream()
                                .map(role -> role.getName())
                                .collect(Collectors.toSet());

        return new UserDto(user.getId(), user.getUsername(), roles);
    }

}

