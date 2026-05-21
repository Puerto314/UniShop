package co.edu.unbosque.unishop.dto;

import java.util.List;

/**
 * Respuesta del endpoint /producto/buscar.
 * Solo Amazon — MercadoLibre eliminado.
 */
public class RespuestaExternaDTO {

    private List<AmazonItemDTO> amazon;

    public RespuestaExternaDTO() {}

    public RespuestaExternaDTO(List<AmazonItemDTO> amazon) {
        this.amazon = amazon;
    }

    public List<AmazonItemDTO> getAmazon()              { return amazon; }
    public void                setAmazon(List<AmazonItemDTO> v) { this.amazon = v; }
}
