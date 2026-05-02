package co.edu.unbosque.unishop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
public class Cliente extends Usuario {

	private String correoElectronico;

	public Cliente() {

	}

	public Cliente(String correoElectronico) {
		super();
		this.correoElectronico = correoElectronico;
	}

	public Cliente(String nombreUsuario, String contraseniaUsuario) {
		super(nombreUsuario, contraseniaUsuario);
	}

	public Cliente(String nombreUsuario, String contraseniaUsuario, String correoElectronico) {
		super(nombreUsuario, contraseniaUsuario);
		this.correoElectronico = correoElectronico;
	}

	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	@Override
	public String toString() {
		return super.toString() + "Cliente [correoElectronico=" + correoElectronico + "]";
	}

}
