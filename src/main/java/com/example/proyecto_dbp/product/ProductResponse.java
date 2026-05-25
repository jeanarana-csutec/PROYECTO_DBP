// ProductResponse.java
package com.example.proyecto_dbp.Product;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class ProductResponse {
    private Long id;
    private String titulo;
    private String descripcion;
    private Double precio;
    private ProductType tipo;
    private ProductStatus estado;
    private String imagenUrl;
    private LocalDateTime fechaPublicacion;
    private String vendedorNombre;
    private String vendedorEmail;
    private String categoryName;
}