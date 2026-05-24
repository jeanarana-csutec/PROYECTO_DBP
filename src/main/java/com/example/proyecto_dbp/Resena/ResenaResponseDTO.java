// ResenaResponse.java
package com.example.proyecto_dbp.Resena;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class ResenaResponseDTO {
    private Long id;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime fecha;
    private String autorNombre;
    private String productoTitulo;
}