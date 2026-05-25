// TransactionRequest.java
package com.example.proyecto_dbp.Transaction;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class TransactionRequest {
    @NotNull
    private Long productId;
    @NotNull
    private TransactionType tipo;
    private LocalDateTime fechaFin;
}