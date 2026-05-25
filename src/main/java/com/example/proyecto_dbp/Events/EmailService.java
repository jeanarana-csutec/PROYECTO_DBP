package com.example.proyecto_dbp.Events;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void enviarEmailBienvenida(String email, String nombre) {
        try {
            Context ctx = new Context();
            ctx.setVariable("nombre", nombre);
            String html = templateEngine.process("emails/bienvenida", ctx);

            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("¡Bienvenido a Economía Circular Estudiantil!");
            helper.setText(html, true);
            mailSender.send(msg);
            log.info("Email de bienvenida enviado a {}", email);
        } catch (Exception e) {
            log.error("Error al enviar email de bienvenida a {}: {}", email, e.getMessage());
        }
    }

    @Async
    public void enviarEmailTransaccionCompletada(String emailComprador,
                                                  String nombreProducto,
                                                  String nombreVendedor) {
        try {
            Context ctx = new Context();
            ctx.setVariable("nombreProducto", nombreProducto);
            ctx.setVariable("nombreVendedor", nombreVendedor);
            String html = templateEngine.process("emails/transaccion-completada", ctx);

            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(emailComprador);
            helper.setSubject("Transacción completada — " + nombreProducto);
            helper.setText(html, true);
            mailSender.send(msg);
            log.info("Email de transacción completada enviado a {}", emailComprador);
        } catch (Exception e) {
            log.error("Error al enviar email de transacción a {}: {}", emailComprador, e.getMessage());
        }
    }

    @Async
    public void enviarEmailNuevoMensaje(String emailReceptor,
                                        String nombreEmisor,
                                        String productoTitulo) {
        try {
            Context ctx = new Context();
            ctx.setVariable("nombreEmisor", nombreEmisor);
            ctx.setVariable("productoTitulo", productoTitulo);
            String html = templateEngine.process("emails/nuevo-mensaje", ctx);

            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(emailReceptor);
            helper.setSubject("Nuevo mensaje sobre — " + productoTitulo);
            helper.setText(html, true);
            mailSender.send(msg);
            log.info("Email de nuevo mensaje enviado a {}", emailReceptor);
        } catch (Exception e) {
            log.error("Error al enviar email de nuevo mensaje a {}: {}", emailReceptor, e.getMessage());
        }
    }
}
