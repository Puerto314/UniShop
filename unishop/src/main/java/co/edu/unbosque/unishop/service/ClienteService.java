package co.edu.unbosque.unishop.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.unishop.dto.ClienteDTO;
import co.edu.unbosque.unishop.entity.Cliente;
import co.edu.unbosque.unishop.repository.ClienteRepository;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private CorreoService correoService;

	public ClienteService() {
	}

	/**
	 * Crea un nuevo cliente.
	 * - Verifica que el nombre de usuario no exista ya.
	 * - Verifica que el correo electronico sea valido (contiene '@' y dominio).
	 * - Si el correo existe y es valido, envia un codigo de verificacion de 6 digitos.
	 *
	 * Retorna:
	 *   1  -> creado exitosamente (correo enviado si es valido)
	 *   -1 -> ya existe un cliente con ese nombre de usuario
	 *   -2 -> formato de correo invalido
	 *   0  -> error inesperado
	 */
	public int create(String nombreUsuario, String contraseniaUsuario, String correoElectronico) {
		try {
			// 1. Validar nombre duplicado
			if (clienteRepository.existsByNombreUsuario(nombreUsuario)) {
				System.err.println("Cliente con nombre '" + nombreUsuario + "' ya existe.");
				return -1;
			}

			// 2. Validar formato de correo
			if (!correoEsValido(correoElectronico)) {
				System.err.println("Correo electronico invalido: " + correoElectronico);
				return -2;
			}

			// 3. Guardar cliente
			Cliente cliente = new Cliente(nombreUsuario, contraseniaUsuario, correoElectronico);
			clienteRepository.save(cliente);

			// 4. Enviar codigo de verificacion al correo
			String codigo = correoService.generarCodigo();
			boolean enviado = correoService.enviarCodigoVerificacion(correoElectronico, codigo);
			if (!enviado) {
				System.err.println("Cliente creado pero no se pudo enviar el correo de verificacion.");
			}

			return 1;

		} catch (Exception e) {
			System.err.println("Error creando cliente: " + e.getMessage());
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
	 * Retorna:
	 *   1  -> actualizado
	 *   -1 -> nombre ya en uso por otro cliente
	 *   -2 -> formato de correo invalido
	 *   0  -> no encontrado
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

	/** Validacion basica de formato de correo electronico. */
	private boolean correoEsValido(String correo) {
		if (correo == null || correo.isBlank()) return false;
		// Debe tener exactamente un '@', con texto antes y dominio con punto despues
		int arroba = correo.indexOf('@');
		if (arroba <= 0) return false;
		String dominio = correo.substring(arroba + 1);
		return dominio.contains(".") && dominio.length() >= 3;
	}
}
