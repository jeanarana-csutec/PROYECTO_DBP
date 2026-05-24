// FavoritoResponse.java
package com.example.proyecto_dbp.Favorito;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class FavoritoResponseDTO {
    private Long id;
    private Long productoId;
    private String productoTitulo;
    private Double productoPrecio;
    private LocalDateTime fechaAgregado;
}