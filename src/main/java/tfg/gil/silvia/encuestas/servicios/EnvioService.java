package tfg.gil.silvia.encuestas.servicios;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import tfg.gil.silvia.encuestas.modelo.Peticion;

@Service
@Slf4j
public class EnvioService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EnvioService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void enviarEncuesta(String baseUrl, Peticion p) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            context.setVariable("encuestaLink", baseUrl+"/encuesta/"+p.getCodigoEncuesta());
            helper.setTo("davidf.76+"+p.getCodigoEncuesta()+"@gmail.com");
            helper.setSubject("Encuesta de satisfacción ["+p.getCodigoEncuesta()+"]");
            helper.setText(templateEngine.process("plantillaCorreo", context), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            // Manejo de excepciones en caso de error al enviar el correo electrónico
            e.printStackTrace();
        }
    }
}

