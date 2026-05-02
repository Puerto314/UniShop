package co.edu.unbosque.unishop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin")
public class Admin extends Usuario {

	private String codigoAdmin;

	public Admin() {

	}

	public Admin(String codigoAdmin) {
		super();
		this.codigoAdmin = codigoAdmin;
	}

	public Admin(String nombreUsuario, String contraseniaUsuario) {
		super(nombreUsuario, contraseniaUsuario);
	}

	public Admin(String nombreUsuario, String contraseniaUsuario, String codigoAdmin) {
		super(nombreUsuario, contraseniaUsuario);
		this.codigoAdmin = codigoAdmin;
	}

	public String getCodigoAdmin() {
		return codigoAdmin;
	}

	public void setCodigoAdmin(String codigoAdmin) {
		this.codigoAdmin = codigoAdmin;
	}

	@Override
	public String toString() {
		return super.toString() + "Admin [codigoAdmin=" + codigoAdmin + "]";
	}

}
