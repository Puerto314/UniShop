package co.edu.unbosque.unishop.dto;

import java.math.BigDecimal;

public class AmazonItemDTO {

    private String asin;
    private String title;
    private BigDecimal price;
    private String url;

    public AmazonItemDTO() {
    }

    public AmazonItemDTO(String asin, String title, BigDecimal price, String url) {
        this.asin = asin;
        this.title = title;
        this.price = price;
        this.url = url;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "AmazonItemDTO [asin=" + asin + ", title=" + title + ", price=" + price + ", url=" + url + "]";
    }
}
