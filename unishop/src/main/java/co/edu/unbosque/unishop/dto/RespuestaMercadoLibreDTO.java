package co.edu.unbosque.unishop.dto;

import java.util.List;

public class RespuestaMercadoLibreDTO {

	private List<MercadoLibreItemDTO> results;

	public RespuestaMercadoLibreDTO() {
	}

	public List<MercadoLibreItemDTO> getResults() {
		return results;
	}

	public void setResults(List<MercadoLibreItemDTO> results) {
		this.results = results;
	}
}
