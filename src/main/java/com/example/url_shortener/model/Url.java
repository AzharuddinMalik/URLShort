package com.example.url_shortener.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@jakarta.persistence.Entity
@Table(name = "urls", indexes = {
        @Index(name = "idx_short_url", columnList = "shortUrl"),
        @Index(name = "idx_long_url", columnList = "longUrl")
})
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String shortUrl;

    @Column(nullable = false, unique = true, length = 768)
    private String longUrl;

    @Column
    private LocalDateTime createdAt;

//    @Column(nullable = false)
//    private int accessCount = 0;

    // Default constructor
    public Url() {}

    // Parameterized constructor
    public Url(Long id, String shortUrl, String longUrl, LocalDateTime createdAt) {
        this.id = id;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getShortUrl() { return shortUrl; }
    public void setShortUrl(String shortUrl) { this.shortUrl = shortUrl; }

    public String getLongUrl() { return longUrl; }
    public void setLongUrl(String longUrl) { this.longUrl = longUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
