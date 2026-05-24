package co.edu.unbosque.unishop.dto;

import java.math.BigDecimal;

public class CrearOrdenRequestDTO {

    private String ordenId;
    private String fecha;       // "YYYY-MM-DD"
    private BigDecimal total;
    private String itemsJson;
    private String status;

    public CrearOrdenRequestDTO() {}

    public String getOrdenId()              { return ordenId; }
    public void setOrdenId(String ordenId)  { this.ordenId = ordenId; }

    public String getFecha()                { return fecha; }
    public void setFecha(String fecha)      { this.fecha = fecha; }

    public BigDecimal getTotal()            { return total; }
    public void setTotal(BigDecimal total)  { this.total = total; }

    public String getItemsJson()                { return itemsJson; }
    public void setItemsJson(String itemsJson)  { this.itemsJson = itemsJson; }

    public String getStatus()               { return status; }
    public void setStatus(String status)    { this.status = status; }
}
