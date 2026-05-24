// UsuarioRegistradoEvent.java
package com.example.proyecto_dbp.Events;

import com.example.proyecto_dbp.User.User;
import org.springframework.context.ApplicationEvent;

public class UsuarioRegistradoEvent extends ApplicationEvent {
    private final User user;

    public UsuarioRegistradoEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() { return user; }
}