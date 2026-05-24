package com.example.proyecto_dbp.Exceptions;

public class ProductoNoDisponible extends RuntimeException {
    public ProductoNoDisponible(String message) {
        super(message);
    }
}
