package co.edu.unbosque.unishop.dto;

import java.math.BigDecimal;

public class ProductoDTO {

	private Long id;
	private String nombreProducto;
	private String descripcionProducto;
	private BigDecimal precioProducto;
	private String tienda;

	public ProductoDTO() {

	}

	public ProductoDTO(String nombreProducto, String descripcionProducto, BigDecimal precioProducto, String tienda) {
		super();
		this.nombreProducto = nombreProducto;
		this.descripcionProducto = descripcionProducto;
		this.precioProducto = precioProducto;
		this.tienda = tienda;
	}

	@Override
	public String toString() {
		return "ProductoDTO [id=" + id + ", nombreProducto=" + nombreProducto + ", descripcionProducto="
				+ descripcionProducto + ", precioProducto=" + precioProducto + ", tienda=" + tienda + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombreProducto() {
		return nombreProducto;
	}

	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}

	public String getDescripcionProducto() {
		return descripcionProducto;
	}

	public void setDescripcionProducto(String descripcionProducto) {
		this.descripcionProducto = descripcionProducto;
	}

	public BigDecimal getPrecioProducto() {
		return precioProducto;
	}

	public void setPrecioProducto(BigDecimal precioProducto) {
		this.precioProducto = precioProducto;
	}

	public String getTienda() {
		return tienda;
	}

	public void setTienda(String tienda) {
		this.tienda = tienda;
	}

}
