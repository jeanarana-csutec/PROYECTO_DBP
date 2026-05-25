// CategoryRequest.java
package com.example.proyecto_dbp.Category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CategoryRequest {
    @NotBlank
    private String nombre;
    private String descripcion;
}