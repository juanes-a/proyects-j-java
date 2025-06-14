package com.example.demo.exception;



/**
 * EXCEPCIÓN CUANDO NO SE ENCUENTRA UN RECURSO
 * - Departamento no encontrado por ID
 * - Usuario no existe
 * - Proyecto no encontrado
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}