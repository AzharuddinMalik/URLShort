package com.example.url_shortener.service;

public class ShortUrlNotFoundException extends RuntimeException {
    public ShortUrlNotFoundException(String shortUrl) {
        super("Short URL not found: " + shortUrl);
    }
}