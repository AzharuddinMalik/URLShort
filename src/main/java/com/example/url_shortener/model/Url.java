package com.example.url_shortener.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String shortUrl;

    @Column(nullable = false, unique = true)
    private String longUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

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
