// UserResponse.java
package com.example.proyecto_dbp.user;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class UserResponse {
    private Long id;
    private String nombre;
    private String email;
    private String universidad;
    private String fotoUrl;
    private LocalDateTime fechaRegistro;
    private Role role;
}