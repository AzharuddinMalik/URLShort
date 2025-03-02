package com.example.url_shortener.service;

import com.example.url_shortener.model.Url;
import com.example.url_shortener.repository.UrlRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlService {
    private final UrlRepository urlRepository;

    @Autowired
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    // Generate a short URL for a given long URL
    public String generateShortUrl(String longUrl) {
        String shortUrl = RandomStringUtils.randomAlphanumeric(6);
        Url url = new Url(null, shortUrl, longUrl, LocalDateTime.now());
        urlRepository.save(url);
        return shortUrl;
    }

    // Retrieve the long URL from the short URL
    public String getLongUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl)
                .map(Url::getLongUrl)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));
    }
}
