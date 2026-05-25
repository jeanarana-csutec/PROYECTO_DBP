// MessageResponse.java
package com.example.proyecto_dbp.Message;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class MessageResponse {
    private Long id;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private Boolean leido;
    private String emisorNombre;
    private String receptorNombre;
    private String productTitle;
}