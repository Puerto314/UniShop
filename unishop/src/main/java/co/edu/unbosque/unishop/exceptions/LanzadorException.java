package co.edu.unbosque.unishop.exceptions;

public class LanzadorException {

	public static void verificarCorreoValido(String entrada) throws CorreoElectronicoException {
		if (entrada == null || entrada.trim().isEmpty()) {
			throw new CorreoElectronicoException();
		}

		entrada = entrada.trim();

		String patronDelCorreo = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(?:[A-Za-z]{2,})$";

		if (!entrada.matches(patronDelCorreo)) {
			throw new CorreoElectronicoException();
		}

		if (!(entrada.endsWith("@unbosque.edu.co") || entrada.endsWith("@gmail.com")
				|| entrada.endsWith("@hotmail.com"))) {
			throw new CorreoElectronicoException();
		}
	}

	public static void verificarContraseñaValida(String entrada) throws ContraseniaUsuarioException {

		if (entrada.length() < 8) {
			throw new ContraseniaUsuarioException();
		}

		boolean tieneMayuscula = false;
		boolean tieneMinuscula = false;
		boolean tieneNumero = false;

		for (char c : entrada.toCharArray()) {
			if (Character.isUpperCase(c)) {
				tieneMayuscula = true;
			} else if (Character.isLowerCase(c)) {
				tieneMinuscula = true;
			} else if (Character.isDigit(c)) {
				tieneNumero = true;
			}
		}

		if (!(tieneMayuscula && tieneMinuscula && tieneNumero)) {
			throw new ContraseniaUsuarioException();
		}

	}

	public static void verificarNombreUsuario(String entrada) throws NombreUsuarioException {
		if (entrada.length() < 4 || entrada.length() > 14) {
			throw new NombreUsuarioException();
		}

		boolean tieneLetra = false;

		for (char c : entrada.toCharArray()) {
			if (Character.isLetter(c)) {
				tieneLetra = true;
			}
		}

		if (!tieneLetra) {
			throw new NombreUsuarioException();
		}

	}

}
