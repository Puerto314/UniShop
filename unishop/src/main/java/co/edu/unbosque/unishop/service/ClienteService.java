package co.edu.unbosque.unishop.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.unishop.dto.ClienteDTO;
import co.edu.unbosque.unishop.entity.Cliente;
import co.edu.unbosque.unishop.repository.ClienteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class ClienteService {

    // Dominios de correo permitidos (mismos que AuthService)
    private static final String[] DOMINIOS_PERMITIDOS = {
        "@gmail.com",
        "@hotmail.com",
        "@unbosque.edu.co"
    };

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private CorreoService correoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ClienteService() {
    }

    /**
     * Crea un nuevo cliente.
     * @return 1 creado, -1 nombre duplicado, -2 correo inválido/dominio no permitido, 0 error
     */
    public int create(String nombreUsuario, String contraseniaUsuario, String correoElectronico) {
        try {
            if (clienteRepository.existsByNombreUsuario(nombreUsuario)) {
                System.err.println("[ClienteService] Cliente '" + nombreUsuario + "' ya existe.");
                return -1;
            }

            if (!correoEsValido(correoElectronico)) {
                System.err.println("[ClienteService] Correo inválido: " + correoElectronico);
                return -2;
            }

            Cliente cliente = new Cliente(nombreUsuario, passwordEncoder.encode(contraseniaUsuario), correoElectronico);
            clienteRepository.save(cliente);

            String codigo = correoService.generarCodigo();
            boolean enviado = correoService.enviarCodigoVerificacion(correoElectronico, codigo);
            if (!enviado) {
                System.err.println("[ClienteService] Cliente creado pero no se pudo enviar el correo.");
            }

            return 1;

        } catch (Exception e) {
            System.err.println("[ClienteService] Error creando cliente: " + e.getMessage());
            return 0;
        }
    }

    public List<ClienteDTO> getAll() {
        List<ClienteDTO> lista = new ArrayList<>();
        clienteRepository.findAll().forEach(c -> lista.add(mapper.map(c, ClienteDTO.class)));
        return lista;
    }

    public int deleteById(Long id) {
        if (!clienteRepository.existsById(id)) return 0;
        clienteRepository.deleteById(id);
        return 1;
    }

    public long count() {
        return clienteRepository.count();
    }

    public boolean exist(Long id) {
        return clienteRepository.existsById(id);
    }

    /**
     * Actualiza datos del cliente.
     * @return 1 actualizado, -1 nombre duplicado, -2 correo inválido, 0 no encontrado
     */
    public int updateById(Long id, String nombreUsuario, String contraseniaUsuario, String correoElectronico) {
        if (!clienteRepository.existsById(id)) return 0;

        if (clienteRepository.existsByNombreUsuario(nombreUsuario)) {
            Cliente existente = clienteRepository.findByNombreUsuario(nombreUsuario).get();
            if (!existente.getId().equals(id)) return -1;
        }

        if (!correoEsValido(correoElectronico)) return -2;

        Cliente cliente = new Cliente(nombreUsuario, contraseniaUsuario, correoElectronico);
        cliente.setId(id);
        clienteRepository.save(cliente);
        return 1;
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
                int arroba = lower.indexOf('@');
                if (arroba > 0) return true;
            }
        }
        return false;
    }
}
