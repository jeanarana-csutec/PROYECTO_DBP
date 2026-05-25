package com.example.proyecto_dbp.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private String token;
    private String refreshToken;
    private String nombre;
    private LocalDateTime fechaRegistro;
    private String universidad;
}