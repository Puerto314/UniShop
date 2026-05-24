package co.edu.unbosque.unishop.security;

import co.edu.unbosque.unishop.repository.AdminRepository;
import co.edu.unbosque.unishop.repository.ClienteRepository;
import co.edu.unbosque.unishop.entity.Admin;
import co.edu.unbosque.unishop.entity.Cliente;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de detalles de usuario para la autenticación JWT.
 * Busca primero en Admins y luego en Clientes usando nombreUsuario.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final AdminRepository adminRepository;
	private final ClienteRepository clienteRepository;

	public UserDetailsServiceImpl(AdminRepository adminRepository, ClienteRepository clienteRepository) {
		this.adminRepository = adminRepository;
		this.clienteRepository = clienteRepository;
	}

	/**
	 * Carga un usuario por su nombreUsuario. Busca primero entre Admins (rol ADMIN)
	 * y luego entre Clientes (rol USER).
	 *
	 * @param username el nombreUsuario a buscar
	 * @return UserDetails listo para Spring Security
	 * @throws UsernameNotFoundException si no existe en ninguna tabla
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// Buscar en Admin primero
		java.util.Optional<Admin> adminOpt = adminRepository.findByNombreUsuario(username);
		if (adminOpt.isPresent()) {
			Admin admin = adminOpt.get();
			return new User(admin.getNombreUsuario(), admin.getContraseniaUsuario(),
					List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
		}

		// Buscar en Cliente
		java.util.Optional<Cliente> clienteOpt = clienteRepository.findByNombreUsuario(username);
		if (clienteOpt.isPresent()) {
			Cliente cliente = clienteOpt.get();
			return new User(cliente.getNombreUsuario(), cliente.getContraseniaUsuario(),
					List.of(new SimpleGrantedAuthority("ROLE_USER")));
		}

		throw new UsernameNotFoundException("Usuario no encontrado: " + username);
	}
}
