package com.example.proyecto_dbp.Producto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoRequestDTO {
    private String titulo;
    private String descripcion;
    private Double precio;
    private TipoProducto tipo;
    private EstadoProducto estado;
    private String imagenUrl;
    private Long vendedorId;  // ID del Usuario que vende el producto
    private Long categoriaId; // ID de la Categoria a la que pertenece
}