package com.example.demo.service;

public class EmailException extends RuntimeException {
    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }
}