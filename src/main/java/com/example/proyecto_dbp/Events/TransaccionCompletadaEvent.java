// TransaccionCompletadaEvent.java
package com.example.proyecto_dbp.Events;

import com.example.proyecto_dbp.Transaccion.Transaccion;
import org.springframework.context.ApplicationEvent;

public class TransaccionCompletadaEvent extends ApplicationEvent {
    private final Transaccion transaccion;

    public TransaccionCompletadaEvent(Object source, Transaccion transaccion) {
        super(source);
        this.transaccion = transaccion;
    }

    public Transaccion getTransaccion() { return transaccion; }
}