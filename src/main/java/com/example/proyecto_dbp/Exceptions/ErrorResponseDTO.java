package com.example.proyecto_dbp.Exceptions;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter @Setter
public class ErrorResponseDTO {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorResponseDTO(int status, String error, String message, String path) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}