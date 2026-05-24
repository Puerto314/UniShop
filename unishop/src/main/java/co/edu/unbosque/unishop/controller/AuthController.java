package co.edu.unbosque.unishop.controller;

import co.edu.unbosque.unishop.dto.AuthRequestDTO;
import co.edu.unbosque.unishop.dto.AuthResponseDTO;
import co.edu.unbosque.unishop.dto.RegisterRequestDTO;
import co.edu.unbosque.unishop.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080", "*" })
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = authService.login(request.getNombreUsuario(), request.getContraseniaUsuario());
        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/enviar-codigo")
    public ResponseEntity<Map<String, String>> enviarCodigo(@RequestBody RegisterRequestDTO request) {
        int resultado = authService.enviarCodigo(
                request.getNombreUsuario(),
                request.getContraseniaUsuario(),
                request.getCorreoElectronico());

        return switch (resultado) {
            case 1  -> ResponseEntity.ok(Map.of("mensaje",
                    "Código de verificación enviado a " + request.getCorreoElectronico()));
            case -1 -> ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "El nombre de usuario '" + request.getNombreUsuario() + "' ya existe"));
            case -2 -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error",
                            "El correo '" + request.getCorreoElectronico() +
                            "' no es válido. Solo se permiten: @gmail.com, @hotmail.com, @unbosque.edu.co"));
            case -3 -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "No se pudo enviar el correo. Verifica la dirección e intenta de nuevo."));
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al enviar el código"));
        };
    }

    @PostMapping("/verificar-y-registrar")
    public ResponseEntity<Map<String, String>> verificarYRegistrar(@RequestBody RegisterRequestDTO request) {
        int resultado = authService.verificarYRegistrar(
                request.getNombreUsuario(),
                request.getContraseniaUsuario(),
                request.getCorreoElectronico(),
                request.getCodigo());

        return switch (resultado) {
            case 1  -> ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Usuario creado correctamente. Ya puedes iniciar sesión."));
            case -1 -> ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "El nombre de usuario '" + request.getNombreUsuario() + "' ya existe"));
            case -2 -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Código de verificación incorrecto o expirado"));
            case -3 -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Primero solicita el código de verificación"));
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear el usuario"));
        };
    }
}
