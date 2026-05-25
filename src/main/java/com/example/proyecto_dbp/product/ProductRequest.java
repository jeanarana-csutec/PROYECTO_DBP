// ProductRequest.java
package com.example.proyecto_dbp.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductRequest {
    @NotBlank
    private String titulo;
    private String descripcion;
    @NotNull @Positive
    private Double precio;
    @NotNull
    private ProductType tipo;
    private String imagenUrl;
    @NotNull
    private Long categoryId;
}