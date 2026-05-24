package co.edu.unbosque.unishop.dto;

public class RegisterRequestDTO {
    private String nombreUsuario;
    private String contraseniaUsuario;
    private String correoElectronico;
    private String codigo;

    public RegisterRequestDTO() {}

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getContraseniaUsuario() { return contraseniaUsuario; }
    public void setContraseniaUsuario(String contraseniaUsuario) { this.contraseniaUsuario = contraseniaUsuario; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}
