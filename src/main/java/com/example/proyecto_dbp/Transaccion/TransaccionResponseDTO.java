// TransaccionResponse.java
package com.example.proyecto_dbp.Transaccion;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class TransaccionResponseDTO {
    private Long id;
    private TipoTransaccion tipo;
    private EstadoTransaccion estado;
    private Double monto;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String compradorNombre;
    private String vendedorNombre;
    private String productoTitulo;
}