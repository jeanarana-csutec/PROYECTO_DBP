package com.example.proyecto_dbp.Exceptions;

public class TransaccionInvalida extends RuntimeException {
    public TransaccionInvalida(String message) {
        super(message);
    }
}
