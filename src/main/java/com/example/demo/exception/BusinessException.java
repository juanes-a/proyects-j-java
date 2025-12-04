package com.example.demo.exception;

/**
 * EXCEPCIÓN PARA ERRORES DE LÓGICA DE NEGOCIO
 * - Validaciones que fallan
 * - Reglas de negocio violadas
 * - Datos inconsistentes
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}