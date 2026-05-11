package co.edu.unbosque.unishop.service;

import java.util.Properties;
import java.util.Random;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

/**
 * Servicio de envio de correos usando Jakarta Mail (SMTP Gmail).
 * Genera y envia un codigo de verificacion de 6 digitos al correo del cliente.
 */
@Service
public class CorreoService {

	private static final String REMITENTE      = "unishop314@gmail.com";
	private static final String CONTRASENIA    = "TiendaUniShop0314#";
	private static final String HOST_SMTP      = "smtp.gmail.com";
	private static final int    PUERTO_SMTP    = 587;

	private final Random random = new Random();

	/**
	 * Genera un codigo de verificacion aleatorio de 6 digitos.
	 * @return codigo como String, ej: "048523"
	 */
	public String generarCodigo() {
		int numero = random.nextInt(900000) + 100000; // garantiza 6 digitos: 100000-999999
		return String.valueOf(numero);
	}

	/**
	 * Envia un correo con el codigo de verificacion al destinatario.
	 *
	 * @param destinatario correo electronico del cliente
	 * @param codigo       codigo de 6 digitos generado
	 * @return true si el correo se envio correctamente, false en caso de error
	 */
	public boolean enviarCodigoVerificacion(String destinatario, String codigo) {
		Properties props = new Properties();
		props.put("mail.smtp.auth",            "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host",            HOST_SMTP);
		props.put("mail.smtp.port",            String.valueOf(PUERTO_SMTP));
		props.put("mail.smtp.ssl.trust",       HOST_SMTP);

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(REMITENTE, CONTRASENIA);
			}
		});

		try {
			MimeMessage mensaje = new MimeMessage(session);
			mensaje.setFrom(new InternetAddress(REMITENTE, "UniShop"));
			mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
			mensaje.setSubject("Codigo de verificacion UniShop");
			mensaje.setText(
				"Hola!\n\n" +
				"Tu codigo de verificacion para UniShop es:\n\n" +
				"   " + codigo + "\n\n" +
				"Este codigo es de uso personal. No lo compartas con nadie.\n\n" +
				"Equipo UniShop"
			);

			Transport.send(mensaje);
			System.out.println("Correo de verificacion enviado a: " + destinatario);
			return true;

		} catch (MessagingException | java.io.UnsupportedEncodingException e) {
			System.err.println("Error enviando correo a " + destinatario + ": " + e.getMessage());
			return false;
		}
	}
}
