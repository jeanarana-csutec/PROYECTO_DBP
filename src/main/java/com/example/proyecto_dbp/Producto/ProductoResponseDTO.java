package com.example.proyecto_dbp.Producto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProductoResponseDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private Double precio;
    private TipoProducto tipo;
    private EstadoProducto estado;
    private String imagenUrl;
    private LocalDateTime fechaPublicacion;

    // Datos aplanados del Vendedor (Usuario)
    private Long vendedorId;
    private String vendedorNombre;

    // Datos aplanados de la Categoría
    private Long categoriaId;
    private String categoriaNombre;
}