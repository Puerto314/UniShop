package co.edu.unbosque.unishop.exceptions;

public class ContraseniaUsuarioException extends Exception {

	public ContraseniaUsuarioException() {
		super("La contraseña debe tener mínimo 8 caracteres, contener números, mayúsculas y minúsculas");
	}

}
