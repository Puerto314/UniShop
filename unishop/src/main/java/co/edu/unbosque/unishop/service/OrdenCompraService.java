package co.edu.unbosque.unishop.service;

import co.edu.unbosque.unishop.dto.CrearOrdenRequestDTO;
import org.springframework.transaction.annotation.Transactional;
import co.edu.unbosque.unishop.dto.OrdenCompraDTO;
import co.edu.unbosque.unishop.entity.Cliente;
import co.edu.unbosque.unishop.entity.OrdenCompra;
import co.edu.unbosque.unishop.repository.ClienteRepository;
import co.edu.unbosque.unishop.repository.OrdenCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdenCompraService {

	@Autowired
	private OrdenCompraRepository ordenRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	/**
	 * Guarda un nuevo pedido asociado al cliente autenticado. Retorna: 1 ok, 0
	 * cliente no encontrado.
	 */
	public int crearOrden(String nombreUsuario, CrearOrdenRequestDTO dto) {
		Cliente cliente = clienteRepository.findByNombreUsuario(nombreUsuario).orElse(null);
		if (cliente == null)
			return 0;

		LocalDate fecha = (dto.getFecha() != null && !dto.getFecha().isBlank())
				? LocalDate.parse(dto.getFecha().split("T")[0])
				: LocalDate.now();

		OrdenCompra orden = new OrdenCompra(cliente, dto.getOrdenId(), fecha, dto.getTotal(), dto.getItemsJson(),
				dto.getStatus() != null ? dto.getStatus() : "pending");
		ordenRepository.save(orden);
		return 1;
	}

	/**
	 * Devuelve los pedidos del cliente autenticado.
	 */
	public List<OrdenCompraDTO> getOrdenesPropias(String nombreUsuario) {
		Cliente cliente = clienteRepository.findByNombreUsuario(nombreUsuario).orElse(null);
		if (cliente == null)
			return List.of();
		return ordenRepository.findByClienteIdOrderByFechaDesc(cliente.getId()).stream().map(this::toDTO)
				.collect(Collectors.toList());
	}

	/**
	 * Devuelve TODOS los pedidos de TODOS los clientes (solo admin).
	 */
	@Transactional(readOnly = true)
	public List<OrdenCompraDTO> getTodasLasOrdenes() {
		return ordenRepository.findAllByOrderByFechaDesc().stream().map(o -> {
			OrdenCompraDTO dto = toDTO(o);
			dto.setClienteNombre(o.getCliente().getNombreUsuario());
			return dto;
		}).collect(Collectors.toList());
	}

	// ── helper ──────────────────────────────────────────────────────────────

	private OrdenCompraDTO toDTO(OrdenCompra o) {
		OrdenCompraDTO dto = new OrdenCompraDTO();
		dto.setId(o.getId());
		dto.setOrdenId(o.getOrdenId());
		dto.setFecha(o.getFecha() != null ? o.getFecha().toString() : "");
		dto.setTotal(o.getTotal());
		dto.setItemsJson(o.getItemsJson());
		dto.setStatus(o.getStatus());
		return dto;
	}
}
