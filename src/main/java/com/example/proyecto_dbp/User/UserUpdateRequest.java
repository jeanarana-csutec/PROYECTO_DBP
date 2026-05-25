// UserUpdateRequest.java
package com.example.proyecto_dbp.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserUpdateRequest {
    @NotBlank
    @Size(min = 2, max = 100)
    private String nombre;

    private String fotoUrl;

    @Size(max = 100)
    private String universidad;
}