package com.example.proyecto_dbp.exception;

public class TransactionInvalidException extends RuntimeException {
    public TransactionInvalidException(String message) {
        super(message);
    }
}
