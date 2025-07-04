package com.example.url_shortener.service;

import com.example.url_shortener.model.Url;
import com.example.url_shortener.repository.UrlRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private static final Pattern CUSTOM_PATH_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,20}$"); // 3-20 chars, alphanumeric, hyphen, underscore

    // Make sure you have this import:
    // import java.util.regex.Pattern;

    @Autowired
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    // UrlService.java
    public String generateShortUrl(String longUrl) {
        // Check if URL already exists
        Optional<Url> existingUrl = urlRepository.findByLongUrl(longUrl);
        if (existingUrl.isPresent()) {
            return existingUrl.get().getShortUrl();
        }

        // Generate unique short URL with collision handling
        String shortUrl;
        do {
            shortUrl = RandomStringUtils.randomAlphanumeric(9); // Changed to 9 characters
        } while (urlRepository.existsByShortUrl(shortUrl));

        Url url = new Url();
        url.setLongUrl(longUrl);
        url.setShortUrl(shortUrl);
        url.setCreatedAt(LocalDateTime.now());
        urlRepository.save(url);
        return shortUrl;
    }


    public String createCustomUrl(String longUrl, String customPath) {
        // 1. Basic validation of custom path
        if (customPath == null || customPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Custom path cannot be empty.");
        }
        customPath = customPath.trim(); // Trim whitespace

        if (!CUSTOM_PATH_PATTERN.matcher(customPath).matches()) {
            throw new IllegalArgumentException("Custom path must be 3-20 characters long and contain only letters, numbers, hyphens, or underscores.");
        }

        // Prevent custom path from clashing with API prefix or reserved words
        if (customPath.toLowerCase().startsWith("api/url") || customPath.toLowerCase().startsWith("css") || customPath.toLowerCase().startsWith("js")) {
            throw new IllegalArgumentException("Custom path cannot start with 'api/url', 'css', or 'js'. Please choose a different path.");
        }

//        // 2. Check if the custom path already exists
//        if (urlRepository.existsByShortUrl(customPath)) {
//            // Throw an exception for clear error to user
//            throw new DuplicateShortUrlException("The custom path '" + customPath + "' is already taken. Please choose another one.");
//        }

        // 3. Save the new custom URL
        Url url = new Url();
        url.setLongUrl(longUrl);
        url.setShortUrl(customPath); // Use customPath as shortUrl
        url.setCreatedAt(LocalDateTime.now());
        urlRepository.save(url);
        System.out.println("Successfully saved custom short URL to DB: " + customPath); // For debugging
        return customPath;
    }

    // Retrieve the long URL from the short URL
    public String getLongUrl(String shortUrl) {

        Url url = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new ShortUrlNotFoundException(shortUrl));

        //java: cannot find symbol
        //  symbol:   method getAccessCount()
        //  location: variable url of type com.example.url_shortener.model.Url/ /
//        url.setAccessCount(url.getAccessCount() + 1);
////        urlRepository.save(url);
//
//        return url.getLongUrl();

        return urlRepository.findByShortUrl(shortUrl)
                .map(Url::getLongUrl)
                .orElseThrow(() -> new ShortUrlNotFoundException(shortUrl));
    }

    public long getUrlCount() {
        return urlRepository.count();
    }

}
