// ResenaRequest.java
package com.example.proyecto_dbp.Resena;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResenaRequestDTO {
    @NotNull
    @Min(1) @Max(5)
    private Integer puntuacion;
    private String comentario;
    @NotNull
    private Long transaccionId;
}