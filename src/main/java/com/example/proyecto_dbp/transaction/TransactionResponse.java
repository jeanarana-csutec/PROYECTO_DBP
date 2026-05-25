// TransactionResponse.java
package com.example.proyecto_dbp.Transaction;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class TransactionResponse {
    private Long id;
    private TransactionType tipo;
    private TransactionStatus estado;
    private Double monto;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String compradorNombre;
    private String vendedorNombre;
    private String productTitle;
}