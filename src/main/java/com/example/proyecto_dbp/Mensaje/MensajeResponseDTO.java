// MensajeResponse.java
package com.example.proyecto_dbp.Mensaje;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class MensajeResponseDTO {
    private Long id;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private Boolean leido;
    private String emisorNombre;
    private String receptorNombre;
    private String productoTitulo;
}