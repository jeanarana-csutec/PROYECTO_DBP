// EmailEventListener.java
package com.example.proyecto_dbp.event;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUsuarioRegistrado(UserRegisteredEvent event) {
        emailService.enviarEmailBienvenida(
                event.getUser().getEmail(),
                event.getUser().getNombre()
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransactionCompletada(TransactionCompletedEvent event) {
        emailService.enviarEmailTransactionCompletada(
                event.getTransaction().getComprador().getEmail(),
                event.getTransaction().getProduct().getTitulo(),
                event.getTransaction().getVendedor().getNombre()
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNuevoMessage(NewMessageEvent event) {
        emailService.enviarEmailNuevoMessage(
                event.getMessage().getReceptor().getEmail(),
                event.getMessage().getEmisor().getNombre(),
                event.getMessage().getProduct().getTitulo()
        );
    }
}