package co.edu.unbosque.unishop.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import co.edu.unbosque.unishop.entity.Cliente;

public interface ClienteRepository extends CrudRepository<Cliente, Long> {

	Optional<Cliente> findByUsuario(String usuario);
}
