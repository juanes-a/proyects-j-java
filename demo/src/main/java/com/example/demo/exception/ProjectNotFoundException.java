package com.example.demo.exception;


public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(String message) {
        super(message);
    }

    public ProjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectNotFoundException(Long projectId) {
        super("Proyecto no encontrado con ID: " + projectId);
    }
}

