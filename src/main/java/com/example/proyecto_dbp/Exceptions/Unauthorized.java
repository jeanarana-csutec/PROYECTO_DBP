package com.example.proyecto_dbp.Exceptions;

public class Unauthorized extends RuntimeException {
    public Unauthorized(String message) {
        super(message);
    }
}
