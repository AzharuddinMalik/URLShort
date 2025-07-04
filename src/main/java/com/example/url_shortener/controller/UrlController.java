package com.example.url_shortener.controller;

import com.example.url_shortener.repository.UrlRepository;
import com.example.url_shortener.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/url")

public class UrlController {

    private final UrlService urlService;
    private final UrlRepository urlRepository;

    @Autowired
    public UrlController(UrlService urlService, UrlRepository urlRepository) {
        this.urlService = urlService;
        this.urlRepository = urlRepository;
    }

    @PostMapping("/shorten")
    public ResponseEntity<Map<String, String>> shortenUrl(@RequestParam String longUrl, @RequestParam(required = false) String customPath) {
        String shortUrl;
        try { // It's good to have this try-catch block to handle exceptions from the service
            if (customPath != null && !customPath.isEmpty()) {
                shortUrl = urlService.createCustomUrl(longUrl, customPath);
            } else {
                shortUrl = urlService.generateShortUrl(longUrl);
            }
            Map<String, String> response = new HashMap<>();
            response.put("shortUrl", "/api/url/" + shortUrl);  // Relative path
            return ResponseEntity.ok(response);
        }
        catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse); // 400 Bad Request for validation errors
        }
//        catch (DuplicateShortUrlException e) { // Make sure this exception class exists
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse); // 409 Conflict for duplicates
//        }
        // General catch-all for unexpected errors (optional, but good for robust APIs)
         catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
         }
    }

    // Redirect from short URL to long URL
    @GetMapping("/{shortUrl}")
    public void redirect(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
        String longUrl = urlService.getLongUrl(shortUrl);
        response.sendRedirect(longUrl);
    }



    //------------------ NEW: Add this in a separate file (WebViewController.java)
    @Controller
    @RequestMapping("/")
    public static class WebViewController{
        // Add this endpoint to serve the homepage
        @GetMapping("/")
        public String home() {
            return "forward:/index.html";
        }

        // END POINT TO SERVE THE ABOUT
        @GetMapping("/about")
        public String about() {
            return "forward:/about.html";
        }

        @GetMapping("/pricing")
        public String pricing() {
            return "forward:/price.html";
        }

    }
}
