package com.example.RBAC.exception;

// Custom exception to handle cases where a user already exists in the system.

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

