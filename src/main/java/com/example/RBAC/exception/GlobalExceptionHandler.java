package com.example.RBAC.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Global exception handler to centralize exception handling across the application.
// This class uses @ControllerAdvice to intercept exceptions thrown by controllers and return appropriate HTTP responses.
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles UserAlreadyExistsException and returns a 409 Conflict response.
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExists(UserAlreadyExistsException e) {
        return ResponseEntity.status(409).body(Map.of("error", e.getMessage())); // 409 Conflict
    }

    // Handles generic RuntimeException and returns a 400 Bad Request response.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
    }

    // Handles ResourceNotFoundException and returns a 404 Not Found response.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(404).body(Map.of("error", e.getMessage())); // 404 Not Found
    }
}
