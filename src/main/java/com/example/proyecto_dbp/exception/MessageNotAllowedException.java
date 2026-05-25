package com.example.proyecto_dbp.exception;

public class MessageNotAllowedException extends RuntimeException {
    public MessageNotAllowedException(String message) {
        super(message);
    }
}
