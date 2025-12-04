package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * MANEJADOR GLOBAL DE EXCEPCIONES
 * - Captura TODAS las excepciones del sistema
 * - Devuelve respuestas HTTP consistentes
 * - Maneja validaciones automáticamente
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * MANEJA ERRORES DE LÓGICA DE NEGOCIO
     * HTTP 400 - Bad Request
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(
                "BUSINESS_ERROR",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * MANEJA RECURSOS NO ENCONTRADOS
     * HTTP 404 - Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                "RESOURCE_NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * MANEJA ERRORES DE AUTORIZACIÓN
     * HTTP 401 - Unauthorized
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        ErrorResponse error = new ErrorResponse(
                "UNAUTHORIZED",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * MANEJA ERRORES DE VALIDACIÓN DE BEAN VALIDATION
     * HTTP 400 - Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                "VALIDATION_ERROR",
                "Errores de validación en los datos enviados",
                errors,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * MANEJA CUALQUIER EXCEPCIÓN NO CONTROLADA
     * HTTP 500 - Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                "INTERNAL_ERROR",
                "Error interno del servidor: " + ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


    /**
     * CLASE PARA RESPUESTAS DE ERROR SIMPLES
     */
    public static class ErrorResponse {
        private String code;
        private String message;
        private LocalDateTime timestamp;

        public ErrorResponse(String code, String message, LocalDateTime timestamp) {
            this.code = code;
            this.message = message;
            this.timestamp = timestamp;
        }

        // Getters
        public String getCode() { return code; }
        public String getMessage() { return message; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }

    /**
     * CLASE PARA RESPUESTAS DE ERROR DE VALIDACIÓN
     */
    public static class ValidationErrorResponse {
        private String code;
        private String message;
        private Map<String, String> fieldErrors;
        private LocalDateTime timestamp;

        public ValidationErrorResponse(String code, String message, Map<String, String> fieldErrors, LocalDateTime timestamp) {
            this.code = code;
            this.message = message;
            this.fieldErrors = fieldErrors;
            this.timestamp = timestamp;
        }


        // Getters
        public String getCode() { return code; }
        public String getMessage() { return message; }
        public Map<String, String> getFieldErrors() { return fieldErrors; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }

}
