// TransactionCompletedEvent.java
package com.example.proyecto_dbp.event;

import com.example.proyecto_dbp.Transaction.Transaction;
import org.springframework.context.ApplicationEvent;

public class TransactionCompletedEvent extends ApplicationEvent {
    private final Transaction transaccion;

    public TransactionCompletedEvent(Object source, Transaction transaccion) {
        super(source);
        this.transaccion = transaccion;
    }

    public Transaction getTransaction() { return transaccion; }
}