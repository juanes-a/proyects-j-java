package com.example.demo.exception;

/**
 * EXCEPCIÓN PARA PROBLEMAS DE AUTORIZACIÓN
 * - Usuario no tiene permisos
 * - Token inválido
 * - Acceso denegado
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}