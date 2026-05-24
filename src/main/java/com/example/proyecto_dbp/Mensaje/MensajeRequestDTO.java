// MensajeRequest.java
package com.example.proyecto_dbp.Mensaje;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MensajeRequestDTO {
    @NotBlank
    private String contenido;
    @NotNull
    private Long receptorId;
    @NotNull
    private Long productoId;
}