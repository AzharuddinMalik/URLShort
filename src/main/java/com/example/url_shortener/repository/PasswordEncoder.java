package com.example.url_shortener.repository;

import org.springframework.stereotype.Service; // Import the Service annotation

/**
 * A simple password encoder for demonstration purposes.
 * In a real application, consider using Spring Security's BCryptPasswordEncoder
 * or a similar robust implementation.
 */
@Service // Mark this class as a Spring Service component
public class PasswordEncoder {
    public String encode(String password) {
        // For production, use a strong hashing algorithm like BCrypt
        return password; // Simple pass-through for now
    }

    public String decode(String password) {
        // Not typically used for actual decoding, but for consistency in a demo
        return password;
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        // For production, compare hashed passwords
        return rawPassword.equals(encodedPassword);
    }

    // This method name is a bit redundant with the class name;
    // consider removing if not explicitly needed, or rename to something clearer.
    public String passwordEncoder(String password) {
        return password;
    }
}
