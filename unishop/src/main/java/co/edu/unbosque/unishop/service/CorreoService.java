package co.edu.unbosque.unishop.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class CorreoService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    private final Random random = new Random();

    public CorreoService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public String generarCodigo() {
        int numero = random.nextInt(900000) + 100000;
        return String.valueOf(numero);
    }

    public boolean enviarCodigoVerificacion(String destinatario, String codigo) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            // multipart=false, charset=UTF-8
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, false, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject("Código de verificación UniShop");

            String cuerpoHtml =
                "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'/></head><body style='font-family:Arial,sans-serif;background:#f4f4f4;padding:30px'>" +
                "<div style='max-width:500px;margin:0 auto;background:#fff;border-radius:10px;padding:30px;box-shadow:0 2px 8px rgba(0,0,0,0.1)'>" +
                "<h2 style='color:#00bcd4;margin-top:0'>UniShop ⚡</h2>" +
                "<p>Tu código de verificación es:</p>" +
                "<div style='font-size:2.5rem;font-weight:bold;letter-spacing:0.4em;color:#222;background:#f0f0f0;border-radius:8px;padding:16px;text-align:center'>" +
                codigo +
                "</div>" +
                "<p style='color:#666;font-size:0.9rem;margin-top:20px'>Este código expira en <strong>10 minutos</strong>. No lo compartas con nadie.</p>" +
                "<hr style='border:none;border-top:1px solid #eee'/>" +
                "<p style='color:#aaa;font-size:0.8rem'>Equipo UniShop &mdash; Universidad El Bosque</p>" +
                "</div></body></html>";

            helper.setText(cuerpoHtml, true); // true = HTML

            mailSender.send(mensaje);
            System.out.println("[CorreoService] Correo enviado a: " + destinatario);
            return true;

        } catch (Exception e) {
            System.err.println("[CorreoService] Error enviando correo a " + destinatario + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
