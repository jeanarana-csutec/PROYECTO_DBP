package com.example.proyecto_dbp.Exceptions;

public class UserAlreadyExists extends RuntimeException {
    public UserAlreadyExists(String message) {
        super(message);
    }
}
