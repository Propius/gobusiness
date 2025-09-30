package com.govtech.scrabble.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the Scrabble application.
 * Provides consistent error responses and proper security measures.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    fieldError -> sanitizeErrorMessage(fieldError.getDefaultMessage()),
                    (existing, replacement) -> existing // Keep first error for duplicate fields
                ));

        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            "Invalid input provided",
            errors,
            HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handle type mismatch errors (e.g., string provided where integer expected)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        logger.warn("Type mismatch error: {}", ex.getMessage());
        
        String message = String.format("Invalid value for parameter '%s'. Expected %s but received: %s",
            ex.getName(),
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown type",
            sanitizeInput(String.valueOf(ex.getValue()))
        );

        ErrorResponse errorResponse = new ErrorResponse(
            "TYPE_MISMATCH",
            message,
            Map.of("parameter", ex.getName()),
            HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Illegal argument error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INVALID_ARGUMENT",
            sanitizeErrorMessage(ex.getMessage()),
            null,
            HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handle illegal state exceptions (service unavailable)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        logger.error("Service unavailable: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "SERVICE_UNAVAILABLE",
            "Service is temporarily unavailable. Please try again later.",
            null,
            HttpStatus.SERVICE_UNAVAILABLE.value()
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    /**
     * Handle generic runtime exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        logger.error("Unexpected runtime error: ", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_ERROR",
            "An unexpected error occurred. Please try again later.",
            null,
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error: ", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_ERROR",
            "An unexpected error occurred. Please try again later.",
            null,
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Sanitize error messages to prevent information leakage
     */
    private String sanitizeErrorMessage(String message) {
        if (message == null) {
            return "Invalid input";
        }
        
        // Limit message length and remove potentially harmful content
        String sanitized = message.trim()
                                 .replaceAll("[\\r\\n\\t]", " ")
                                 .replaceAll("\\s+", " ");
        
        return sanitized.length() > 200 ? sanitized.substring(0, 200) + "..." : sanitized;
    }

    /**
     * Sanitize user input for safe logging
     */
    private String sanitizeInput(String input) {
        if (input == null) {
            return "null";
        }
        
        // Limit length and remove control characters
        String sanitized = input.replaceAll("[\\p{Cntrl}]", "")
                               .trim();
        
        return sanitized.length() > 50 ? sanitized.substring(0, 50) + "..." : sanitized;
    }

    /**
     * Standard error response structure
     */
    public static class ErrorResponse {
        private final String error;
        private final String message;
        private final Map<String, String> details;
        private final int status;
        private final LocalDateTime timestamp;

        public ErrorResponse(String error, String message, Map<String, String> details, int status) {
            this.error = error;
            this.message = message;
            this.details = details;
            this.status = status;
            this.timestamp = LocalDateTime.now();
        }

        public String getError() { return error; }
        public String getMessage() { return message; }
        public Map<String, String> getDetails() { return details; }
        public int getStatus() { return status; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}