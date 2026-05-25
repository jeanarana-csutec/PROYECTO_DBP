// ReviewResponse.java
package com.example.proyecto_dbp.Review;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class ReviewResponse {
    private Long id;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime fecha;
    private String autorNombre;
    private String productTitle;
}