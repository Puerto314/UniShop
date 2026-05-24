package co.edu.unbosque.unishop.configuration;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.unishop.entity.Admin;
import co.edu.unbosque.unishop.entity.Cliente;
import co.edu.unbosque.unishop.repository.AdminRepository;
import co.edu.unbosque.unishop.repository.ClienteRepository;

/**
 * Carga datos iniciales en la base de datos al arrancar la aplicación. Crea un
 * Admin y un Cliente de prueba si no existen previamente.
 */
@Configuration
public class LoadDatabase {

	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	/** Código fijo asignado a todos los admins (igual que AdminService). */
	private static final String CODIGO_ADMIN_FIJO = "Puerto314";

	/**
	 * Inicializa un Admin y un Cliente predeterminados.
	 *
	 * @param adminRepository   Repositorio de administradores
	 * @param clienteRepository Repositorio de clientes
	 * @param passwordEncoder   Codificador BCrypt para las contraseñas
	 * @return CommandLineRunner que se ejecuta al iniciar la aplicación
	 */
	@Bean
	CommandLineRunner initDatabase(AdminRepository adminRepository, ClienteRepository clienteRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {

			// ── Admin predeterminado ──────────────────────────────────────
			Optional<Admin> adminExistente = adminRepository.findByNombreUsuario("admin");
			if (adminExistente.isPresent()) {
				log.info("El administrador ya existe, omitiendo creación...");
			} else {
				Admin adminUser = new Admin("admin", passwordEncoder.encode("1234567890"), CODIGO_ADMIN_FIJO);
				adminRepository.save(adminUser);
				log.info("Precargando administrador predeterminado");
			}

			// ── Cliente predeterminado ────────────────────────────────────
			Optional<Cliente> clienteExistente = clienteRepository.findByNombreUsuario("clientenormal");
			if (clienteExistente.isPresent()) {
				log.info("El cliente normal ya existe, omitiendo creación...");
			} else {
				Cliente clienteUser = new Cliente("clientenormal", passwordEncoder.encode("1234567890"),
						"clientenormal@unishop.com");
				clienteRepository.save(clienteUser);
				log.info("Precargando cliente predeterminado");
			}
		};
	}
}
