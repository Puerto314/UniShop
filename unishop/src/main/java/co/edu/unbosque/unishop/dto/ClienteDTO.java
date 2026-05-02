package co.edu.unbosque.unishop.dto;

public class ClienteDTO {

	private String nombreUsuario;
	private String contraseniaUsuario;
	private Long id;
	private String correoElectronico;

	public ClienteDTO() {

	}

	public ClienteDTO(String nombreUsuario, String contraseniaUsuario, String correoElectronico) {
		super();
		this.nombreUsuario = nombreUsuario;
		this.contraseniaUsuario = contraseniaUsuario;
		this.correoElectronico = correoElectronico;
	}

	@Override
	public String toString() {
		return "ClienteDTO [nombreUsuario=" + nombreUsuario + ", contraseniaUsuario=" + contraseniaUsuario + ", id="
				+ id + ", correoElectronico=" + correoElectronico + "]";
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getContraseniaUsuario() {
		return contraseniaUsuario;
	}

	public void setContraseniaUsuario(String contraseniaUsuario) {
		this.contraseniaUsuario = contraseniaUsuario;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

}
