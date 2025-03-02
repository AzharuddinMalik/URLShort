package com.example.url_shortener.controller;

import com.example.url_shortener.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/url")
public class UrlController {

    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    // Endpoint to shorten a URL
    @PostMapping("/shorten")
    public ResponseEntity<Map<String, String>> shortenUrl(@RequestParam String longUrl) {
        String shortUrl = urlService.generateShortUrl(longUrl);
        Map<String, String> response = new HashMap<>();
        response.put("shortUrl", "http://localhost:8080/api/url/" + shortUrl);
        return ResponseEntity.ok(response);
    }

    // Redirect from short URL to long URL
    @GetMapping("/{shortUrl}")
    public void redirect(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
        String longUrl = urlService.getLongUrl(shortUrl);
        response.sendRedirect(longUrl);
    }
}
