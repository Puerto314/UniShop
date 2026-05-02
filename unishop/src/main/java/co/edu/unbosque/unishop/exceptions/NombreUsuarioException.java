package co.edu.unbosque.unishop.exceptions;

public class NombreUsuarioException extends Exception {

	public NombreUsuarioException() {
		super("Ingrese un nombre de usuario válido (entre 4 y 14 caracteres)");
	}

}
