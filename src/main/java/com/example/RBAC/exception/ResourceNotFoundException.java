package com.example.RBAC.exception;

//Custom exception to handle cases where a requested resource is not found.

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
