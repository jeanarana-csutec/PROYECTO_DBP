package com.example.proyecto_dbp.Favorito;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoritoRequestDTO {
    private Long usuarioId;
    private Long productoId;
}