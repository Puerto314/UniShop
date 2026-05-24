package co.edu.unbosque.unishop.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "orden_compra")
public class OrdenCompra {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Referencia al cliente dueño del pedido */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cliente_id", nullable = false)
	private Cliente cliente;

	/** ID legible en el frontend, ej: "ORD-2026-001" */
	@Column(nullable = false)
	private String ordenId;

	private LocalDate fecha;

	private BigDecimal total;

	/** JSON serializado con los items del pedido */
	@Column(columnDefinition = "TEXT")
	private String itemsJson;

	/** pending | processing | shipped | delivered | cancelled */
	private String status;

	public OrdenCompra() {
	}

	public OrdenCompra(Cliente cliente, String ordenId, LocalDate fecha, BigDecimal total, String itemsJson,
			String status) {
		this.cliente = cliente;
		this.ordenId = ordenId;
		this.fecha = fecha;
		this.total = total;
		this.itemsJson = itemsJson;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public String getOrdenId() {
		return ordenId;
	}

	public void setOrdenId(String ordenId) {
		this.ordenId = ordenId;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getItemsJson() {
		return itemsJson;
	}

	public void setItemsJson(String itemsJson) {
		this.itemsJson = itemsJson;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
