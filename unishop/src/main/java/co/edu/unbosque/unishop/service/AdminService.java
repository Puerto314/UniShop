package co.edu.unbosque.unishop.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.unishop.dto.AdminDTO;
import co.edu.unbosque.unishop.entity.Admin;
import co.edu.unbosque.unishop.repository.AdminRepository;

@Service
public class AdminService {

	/** Codigo fijo que se asigna a todos los admins sin excepcion. */
	private static final String CODIGO_ADMIN_FIJO = "Puerto314";

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private ModelMapper mapper;

	public AdminService() {
	}

	/**
	 * Crea un nuevo admin con codigoAdmin fijo "Puerto314".
	 * Retorna:
	 *   1  -> creado exitosamente
	 *   -1 -> ya existe un admin con ese nombre de usuario
	 *   0  -> error inesperado
	 */
	public int create(String nombreUsuario, String contraseniaUsuario) {
		try {
			if (adminRepository.existsByNombreUsuario(nombreUsuario)) {
				System.err.println("Admin con nombre '" + nombreUsuario + "' ya existe.");
				return -1;
			}
			Admin admin = new Admin(nombreUsuario, contraseniaUsuario, CODIGO_ADMIN_FIJO);
			adminRepository.save(admin);
			return 1;
		} catch (Exception e) {
			System.err.println("Error creando admin: " + e.getMessage());
			return 0;
		}
	}

	public List<AdminDTO> getAll() {
		List<AdminDTO> lista = new ArrayList<>();
		adminRepository.findAll().forEach(a -> lista.add(mapper.map(a, AdminDTO.class)));
		return lista;
	}

	public int deleteById(Long id) {
		if (!adminRepository.existsById(id)) return 0;
		adminRepository.deleteById(id);
		return 1;
	}

	public long count() {
		return adminRepository.count();
	}

	public boolean exist(Long id) {
		return adminRepository.existsById(id);
	}

	/**
	 * Actualiza nombre y contrasenia. El codigoAdmin siempre se mantiene "Puerto314".
	 * Retorna:
	 *   1  -> actualizado
	 *   -1 -> el nuevo nombre ya esta en uso por otro admin
	 *   0  -> no encontrado
	 */
	public int updateById(Long id, String nombreUsuario, String contraseniaUsuario) {
		if (!adminRepository.existsById(id)) return 0;
		// Verifica que el nuevo nombre no lo use otro admin distinto
		if (adminRepository.existsByNombreUsuario(nombreUsuario)) {
			Admin existente = adminRepository.findByNombreUsuario(nombreUsuario).get();
			if (!existente.getId().equals(id)) {
				return -1;
			}
		}
		Admin admin = new Admin(nombreUsuario, contraseniaUsuario, CODIGO_ADMIN_FIJO);
		admin.setId(id);
		adminRepository.save(admin);
		return 1;
	}

}
