// EmailService.java
package com.example.proyecto_dbp.Events;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void enviarEmailBienvenida(String email, String nombre) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("¡Bienvenido a Economía Circular Estudiantil!");
        message.setText("Hola " + nombre + ",\n\n" +
                "Tu cuenta ha sido creada exitosamente.\n" +
                "Ya puedes comprar, vender y alquilar materiales académicos.\n\n" +
                "¡Bienvenido!");
        mailSender.send(message);
    }

    @Async
    public void enviarEmailTransaccionCompletada(String emailComprador,
                                                 String nombreProducto,
                                                 String nombreVendedor) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailComprador);
        message.setSubject("Transacción completada — " + nombreProducto);
        message.setText("Tu transacción por el producto '" + nombreProducto +
                "' con el vendedor " + nombreVendedor + " ha sido completada.\n\n" +
                "Recuerda dejar una reseña de tu experiencia.");
        mailSender.send(message);
    }

    @Async
    public void enviarEmailNuevoMensaje(String emailReceptor,
                                        String nombreEmisor,
                                        String productoTitulo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailReceptor);
        message.setSubject("Nuevo mensaje sobre — " + productoTitulo);
        message.setText("Tienes un nuevo mensaje de " + nombreEmisor +
                " sobre el producto '" + productoTitulo + "'.\n\n" +
                "Ingresa a la plataforma para responder.");
        mailSender.send(message);
    }
}