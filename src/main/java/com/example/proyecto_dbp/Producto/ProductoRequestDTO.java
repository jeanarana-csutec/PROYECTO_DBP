// ProductoRequest.java
package com.example.proyecto_dbp.Producto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductoRequestDTO {
    @NotBlank
    private String titulo;
    private String descripcion;
    @NotNull @Positive
    private Double precio;
    @NotNull
    private TipoProducto tipo;
    private String imagenUrl;
    @NotNull
    private Long categoriaId;
}