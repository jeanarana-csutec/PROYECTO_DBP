// NuevoMensajeEvent.java
package com.example.proyecto_dbp.Events;

import com.example.proyecto_dbp.Mensaje.Mensaje;
import org.springframework.context.ApplicationEvent;

public class NuevoMensajeEvent extends ApplicationEvent {
    private final Mensaje mensaje;

    public NuevoMensajeEvent(Object source, Mensaje mensaje) {
        super(source);
        this.mensaje = mensaje;
    }

    public Mensaje getMensaje() { return mensaje; }
}