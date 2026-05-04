package co.edu.unbosque.unishop.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import co.edu.unbosque.unishop.entity.Admin;

public interface AdminRepository extends CrudRepository<Admin, Long> {

	Optional<Admin> findById(Long id);
}
