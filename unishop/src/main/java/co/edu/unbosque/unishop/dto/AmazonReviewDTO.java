package co.edu.unbosque.unishop.dto;

public class AmazonReviewDTO {

    private String author;
    private String title;
    private String body;
    private Double rating;
    private String date;

    public AmazonReviewDTO() {}

    public String getAuthor()              { return author; }
    public void   setAuthor(String v)      { this.author = v; }

    public String getTitle()               { return title; }
    public void   setTitle(String v)       { this.title = v; }

    public String getBody()                { return body; }
    public void   setBody(String v)        { this.body = v; }

    public Double getRating()              { return rating; }
    public void   setRating(Double v)      { this.rating = v; }

    public String getDate()                { return date; }
    public void   setDate(String v)        { this.date = v; }
}
