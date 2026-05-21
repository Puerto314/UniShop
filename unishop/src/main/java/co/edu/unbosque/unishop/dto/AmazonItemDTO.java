package co.edu.unbosque.unishop.dto;

import java.math.BigDecimal;

public class AmazonItemDTO {

    private String asin;
    private String title;
    private BigDecimal price;
    private String url;
    private String imageUrl;
    private Double rating;
    private Integer reviewCount;

    public AmazonItemDTO() {}

    public AmazonItemDTO(String asin, String title, BigDecimal price, String url) {
        this.asin = asin;
        this.title = title;
        this.price = price;
        this.url = url;
    }

    public String getAsin()                          { return asin; }
    public void   setAsin(String asin)               { this.asin = asin; }

    public String getTitle()                         { return title; }
    public void   setTitle(String title)             { this.title = title; }

    public BigDecimal getPrice()                     { return price; }
    public void       setPrice(BigDecimal price)     { this.price = price; }

    public String getUrl()                           { return url; }
    public void   setUrl(String url)                 { this.url = url; }

    public String getImageUrl()                      { return imageUrl; }
    public void   setImageUrl(String imageUrl)       { this.imageUrl = imageUrl; }

    public Double getRating()                        { return rating; }
    public void   setRating(Double rating)           { this.rating = rating; }

    public Integer getReviewCount()                  { return reviewCount; }
    public void    setReviewCount(Integer n)         { this.reviewCount = n; }

    @Override
    public String toString() {
        return "AmazonItemDTO[asin=" + asin + ", title=" + title + ", price=" + price + "]";
    }
}
