package co.edu.unbosque.unishop.dto;

public class AdminDTO {

	private String nombreUsuario;
	private String contraseniaUsuario;
	private Long id;
	private String codigoAdmin;

	public AdminDTO() {

	}

	public AdminDTO(String nombreUsuario, String contraseniaUsuario, String codigoAdmin) {
		super();
		this.nombreUsuario = nombreUsuario;
		this.contraseniaUsuario = contraseniaUsuario;
		this.codigoAdmin = codigoAdmin;
	}

	@Override
	public String toString() {
		return "AdminDTO [nombreUsuario=" + nombreUsuario + ", contraseniaUsuario=" + contraseniaUsuario + ", id=" + id
				+ ", codigoAdmin=" + codigoAdmin + "]";
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

	public String getCodigoAdmin() {
		return codigoAdmin;
	}

	public void setCodigoAdmin(String codigoAdmin) {
		this.codigoAdmin = codigoAdmin;
	}

}
