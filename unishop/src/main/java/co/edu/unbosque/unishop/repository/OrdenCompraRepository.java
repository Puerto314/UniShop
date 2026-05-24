package co.edu.unbosque.unishop.repository;

import co.edu.unbosque.unishop.entity.OrdenCompra;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrdenCompraRepository extends CrudRepository<OrdenCompra, Long> {

    /** Pedidos de un cliente específico, ordenados del más reciente al más antiguo */
    List<OrdenCompra> findByClienteIdOrderByFechaDesc(Long clienteId);

    /** Todos los pedidos de todos los clientes (solo admin) */
    List<OrdenCompra> findAllByOrderByFechaDesc();
}
