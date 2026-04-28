package com.example.user_auth_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return error(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    // Bad credentials (wrong password)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return error(HttpStatus.UNAUTHORIZED, "Invalid username or password", null);
    }

    // Not authenticated
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuth(AuthenticationException ex) {
        return error(HttpStatus.UNAUTHORIZED, "Authentication required", null);
    }

    // Forbidden (wrong role)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(AccessDeniedException ex) {
        return error(HttpStatus.FORBIDDEN, "Access denied — insufficient permissions", null);
    }

    // Business logic errors
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    // Catch-all
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", null);
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message, List<String> details) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", message);
        if (details != null) body.put("details", details);
        return ResponseEntity.status(status).body(body);
    }
}
