package co.edu.unbosque.unishop.dto;

import java.math.BigDecimal;
import com.google.gson.annotations.SerializedName;

public class MercadoLibreItemDTO {

    private String id;
    private String title;
    private BigDecimal price;
    private String permalink;
    private String thumbnail;

    @SerializedName("currency_id")
    private String currencyId;

    private String condition;

    @SerializedName("available_quantity")
    private int availableQuantity;

    @SerializedName("sold_quantity")
    private int soldQuantity;

    public MercadoLibreItemDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getPermalink() { return permalink; }
    public void setPermalink(String permalink) { this.permalink = permalink; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public String getCurrencyId() { return currencyId; }
    public void setCurrencyId(String currencyId) { this.currencyId = currencyId; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    public int getSoldQuantity() { return soldQuantity; }
    public void setSoldQuantity(int soldQuantity) { this.soldQuantity = soldQuantity; }

    @Override
    public String toString() {
        return "MercadoLibreItemDTO [id=" + id + ", title=" + title
                + ", price=" + price + ", permalink=" + permalink
                + ", condition=" + condition + "]";
    }
}
