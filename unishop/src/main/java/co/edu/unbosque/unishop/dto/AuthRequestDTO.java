package co.edu.unbosque.unishop.dto;

public class AuthRequestDTO {
    private String nombreUsuario;
    private String contraseniaUsuario;

    public AuthRequestDTO() {}

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getContraseniaUsuario() { return contraseniaUsuario; }
    public void setContraseniaUsuario(String contraseniaUsuario) { this.contraseniaUsuario = contraseniaUsuario; }
}
