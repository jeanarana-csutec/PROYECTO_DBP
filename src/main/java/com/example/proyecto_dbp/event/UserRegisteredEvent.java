// UserRegisteredEvent.java
package com.example.proyecto_dbp.event;

import com.example.proyecto_dbp.user.User;
import org.springframework.context.ApplicationEvent;

public class UserRegisteredEvent extends ApplicationEvent {
    private final User user;

    public UserRegisteredEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() { return user; }
}