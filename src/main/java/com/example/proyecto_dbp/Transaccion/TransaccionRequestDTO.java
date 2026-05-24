// TransaccionRequest.java
package com.example.proyecto_dbp.Transaccion;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class TransaccionRequestDTO {
    @NotNull
    private Long productoId;
    @NotNull
    private TipoTransaccion tipo;
    private LocalDateTime fechaFin;
}