package com.example.proyecto_dbp.Auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRefreshRequest {
    @NotBlank
    private String refreshToken;
}
