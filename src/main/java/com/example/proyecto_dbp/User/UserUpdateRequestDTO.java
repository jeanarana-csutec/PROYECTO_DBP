// UserUpdateRequest.java
package com.example.proyecto_dbp.User;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserUpdateRequestDTO {
    private String nombre;
    private String fotoUrl;
    private String universidad;
}