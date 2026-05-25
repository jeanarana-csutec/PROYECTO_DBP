// MessageRequest.java
package com.example.proyecto_dbp.Message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MessageRequest {
    @NotBlank
    private String contenido;
    @NotNull
    private Long receptorId;
    @NotNull
    private Long productId;
}