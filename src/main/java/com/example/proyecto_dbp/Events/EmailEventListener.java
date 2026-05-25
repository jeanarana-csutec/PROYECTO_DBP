// EmailEventListener.java
package com.example.proyecto_dbp.Events;

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
    public void onUsuarioRegistrado(UsuarioRegistradoEvent event) {
        emailService.enviarEmailBienvenida(
                event.getUser().getEmail(),
                event.getUser().getNombre()
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransaccionCompletada(TransaccionCompletadaEvent event) {
        emailService.enviarEmailTransaccionCompletada(
                event.getTransaccion().getComprador().getEmail(),
                event.getTransaccion().getProducto().getTitulo(),
                event.getTransaccion().getVendedor().getNombre()
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNuevoMensaje(NuevoMensajeEvent event) {
        emailService.enviarEmailNuevoMensaje(
                event.getMensaje().getReceptor().getEmail(),
                event.getMensaje().getEmisor().getNombre(),
                event.getMensaje().getProducto().getTitulo()
        );
    }
}