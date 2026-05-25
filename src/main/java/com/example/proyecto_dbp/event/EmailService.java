package com.example.proyecto_dbp.event;

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
            helper.setSubject("ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¡Bienvenido a EconomÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a Circular Estudiantil!");
            helper.setText(html, true);
            mailSender.send(msg);
            log.info("Email de bienvenida enviado a {}", email);
        } catch (Exception e) {
            log.error("Error al enviar email de bienvenida a {}: {}", email, e.getMessage());
        }
    }

    @Async
    public void enviarEmailTransactionCompletada(String emailComprador,
                                                  String nombreProduct,
                                                  String nombreVendedor) {
        try {
            Context ctx = new Context();
            ctx.setVariable("nombreProduct", nombreProduct);
            ctx.setVariable("nombreVendedor", nombreVendedor);
            String html = templateEngine.process("emails/transaccion-completada", ctx);

            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(emailComprador);
            helper.setSubject("TransacciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n completada ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â " + nombreProduct);
            helper.setText(html, true);
            mailSender.send(msg);
            log.info("Email de transacciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n completada enviado a {}", emailComprador);
        } catch (Exception e) {
            log.error("Error al enviar email de transacciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n a {}: {}", emailComprador, e.getMessage());
        }
    }

    @Async
    public void enviarEmailNuevoMessage(String emailReceptor,
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
            helper.setSubject("Nuevo mensaje sobre ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â " + productoTitulo);
            helper.setText(html, true);
            mailSender.send(msg);
            log.info("Email de nuevo mensaje enviado a {}", emailReceptor);
        } catch (Exception e) {
            log.error("Error al enviar email de nuevo mensaje a {}: {}", emailReceptor, e.getMessage());
        }
    }
}
