package com.example.proyecto_dbp.Auth;

import com.example.proyecto_dbp.User.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank @Size(min = 8)
    private String password;
}
