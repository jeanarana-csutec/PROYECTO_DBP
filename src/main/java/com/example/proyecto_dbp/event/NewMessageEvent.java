// NewMessageEvent.java
package com.example.proyecto_dbp.event;

import com.example.proyecto_dbp.Message.Message;
import org.springframework.context.ApplicationEvent;

public class NewMessageEvent extends ApplicationEvent {
    private final Message mensaje;

    public NewMessageEvent(Object source, Message mensaje) {
        super(source);
        this.mensaje = mensaje;
    }

    public Message getMessage() { return mensaje; }
}