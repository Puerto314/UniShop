package co.edu.unbosque.unishop.dto;

public class AuthResponseDTO {
    private String token;
    private String nombreUsuario;
    private String rol;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String token, String nombreUsuario, String rol) {
        this.token = token;
        this.nombreUsuario = nombreUsuario;
        this.rol = rol;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
