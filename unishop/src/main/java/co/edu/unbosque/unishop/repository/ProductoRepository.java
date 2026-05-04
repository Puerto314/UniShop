package co.edu.unbosque.unishop.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import co.edu.unbosque.unishop.entity.Producto;

public interface ProductoRepository extends CrudRepository<Producto, Long> {

	Optional<Producto> findById(Long id);
}
