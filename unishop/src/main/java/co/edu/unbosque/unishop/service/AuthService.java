package co.edu.unbosque.unishop.service;

import co.edu.unbosque.unishop.dto.AuthResponseDTO;
import co.edu.unbosque.unishop.entity.Cliente;
import co.edu.unbosque.unishop.repository.ClienteRepository;
import co.edu.unbosque.unishop.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    // Dominios de correo permitidos
    private static final String[] DOMINIOS_PERMITIDOS = {
        "@gmail.com",
        "@hotmail.com",
        "@unbosque.edu.co"
    };

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CorreoService correoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Map<String, PendingRegistration> pendingRegistrations = new ConcurrentHashMap<>();

    private static class PendingRegistration {
        final String codigo;
        final String correo;
        final String contraseniaPlana;
        final Instant expira;

        PendingRegistration(String codigo, String correo, String contraseniaPlana) {
            this.codigo = codigo;
            this.correo = correo;
            this.contraseniaPlana = contraseniaPlana;
            this.expira = Instant.now().plusSeconds(600);
        }

        boolean expirado() {
            return Instant.now().isAfter(expira);
        }
    }

    /**
     * Autentica al usuario y genera un JWT.
     * @return AuthResponseDTO con token, nombreUsuario y rol, o null si falla.
     */
    public AuthResponseDTO login(String nombreUsuario, String contrasenia) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(nombreUsuario, contrasenia));

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            String rol = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst().orElse("ROLE_USER");

            return new AuthResponseDTO(token, userDetails.getUsername(), rol);

        } catch (AuthenticationException e) {
            System.err.println("[AuthService] Fallo de autenticación para '" + nombreUsuario + "': " + e.getMessage());
            return null;
        }
    }

    /**
     * Valida los datos, guarda un registro pendiente y envía el código al correo.
     * @return 1 OK, -1 nombre duplicado, -2 correo inválido/dominio no permitido, -3 error al enviar correo, 0 error
     */
    public int enviarCodigo(String nombreUsuario, String contrasenia, String correo) {
        try {
            if (clienteRepository.existsByNombreUsuario(nombreUsuario)) {
                return -1;
            }

            if (!correoEsValido(correo)) {
                return -2;
            }

            String codigo = correoService.generarCodigo();
            pendingRegistrations.put(nombreUsuario,
                    new PendingRegistration(codigo, correo, contrasenia));

            boolean enviado = correoService.enviarCodigoVerificacion(correo, codigo);
            if (!enviado) {
                pendingRegistrations.remove(nombreUsuario);
                return -3;
            }

            return 1;

        } catch (Exception e) {
            System.err.println("[AuthService] Error en enviarCodigo: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Verifica el código y crea el usuario si es correcto.
     * @return 1 creado, -1 nombre duplicado, -2 código incorrecto/expirado, -3 sin registro pendiente, 0 error
     */
    public int verificarYRegistrar(String nombreUsuario, String contrasenia, String correo, String codigo) {
        try {
            PendingRegistration pending = pendingRegistrations.get(nombreUsuario);
            if (pending == null) {
                return -3;
            }

            if (pending.expirado() || !pending.codigo.equals(codigo)) {
                pendingRegistrations.remove(nombreUsuario);
                return -2;
            }

            if (clienteRepository.existsByNombreUsuario(nombreUsuario)) {
                pendingRegistrations.remove(nombreUsuario);
                return -1;
            }

            Cliente cliente = new Cliente(
                    nombreUsuario,
                    passwordEncoder.encode(pending.contraseniaPlana),
                    pending.correo);
            clienteRepository.save(cliente);

            pendingRegistrations.remove(nombreUsuario);
            System.out.println("[AuthService] Cliente creado: " + nombreUsuario);
            return 1;

        } catch (Exception e) {
            System.err.println("[AuthService] Error en verificarYRegistrar: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Valida que el correo pertenezca a un dominio permitido:
     * @gmail.com, @hotmail.com, @unbosque.edu.co
     */
    private boolean correoEsValido(String correo) {
        if (correo == null || correo.isBlank()) return false;
        String lower = correo.trim().toLowerCase();
        for (String dominio : DOMINIOS_PERMITIDOS) {
            if (lower.endsWith(dominio) && lower.indexOf('@') == lower.lastIndexOf('@')) {
                // Asegurar que haya texto antes del @
                int arroba = lower.indexOf('@');
                if (arroba > 0) return true;
            }
        }
        return false;
    }
}
