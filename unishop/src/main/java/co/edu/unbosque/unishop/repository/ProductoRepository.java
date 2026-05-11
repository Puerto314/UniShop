package co.edu.unbosque.unishop.repository;

import org.springframework.data.repository.CrudRepository;
import co.edu.unbosque.unishop.entity.Producto;

public interface ProductoRepository extends CrudRepository<Producto, Long> {
	// findById ya está definido en CrudRepository<Producto, Long>
}
