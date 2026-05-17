package com.example.proyecto_dbp.Favorito;

import com.example.proyecto_dbp.Producto.ProductoResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FavoritoResponseDTO {
    private Long id;
    private LocalDateTime fechaAgregado;
    private ProductoResponseDTO producto;
}