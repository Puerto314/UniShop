package co.edu.unbosque.unishop.dto;

import java.util.List;

public class RespuestaExternaDTO {

    private List<ProductoDTO> mercadoLibre;
    private List<ProductoDTO> amazon;

    /**
     * URL lista para que el FRONTEND llame directamente a MercadoLibre.
     * Los servidores cloud/ngrok están bloqueados por ML; el browser NO.
     */
    private String mercadoLibreUrl;

    public RespuestaExternaDTO() {}

    public RespuestaExternaDTO(List<ProductoDTO> mercadoLibre, List<ProductoDTO> amazon) {
        this.mercadoLibre = mercadoLibre;
        this.amazon = amazon;
    }

    public List<ProductoDTO> getMercadoLibre() { return mercadoLibre; }
    public void setMercadoLibre(List<ProductoDTO> ml) { this.mercadoLibre = ml; }

    public List<ProductoDTO> getAmazon() { return amazon; }
    public void setAmazon(List<ProductoDTO> amazon) { this.amazon = amazon; }

    public String getMercadoLibreUrl() { return mercadoLibreUrl; }
    public void setMercadoLibreUrl(String url) { this.mercadoLibreUrl = url; }
}
