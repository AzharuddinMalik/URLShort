# XURL - Efficient URL Shortening Service

![Java](https://img.shields.io/badge/Java-11-orange)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen)
![License](https://img.shields.io/badge/License-MIT-blue)

XURL is a high-performance URL shortening library written in Java that transforms long, unwieldy URLs into concise, shareable links while maintaining a 1:1 mapping and tracking usage analytics.

## 🚀 Features

- **URL Shortening**: Generate compact 9-character URLs from long addresses
- **Custom URLs**: Support for user-defined custom short URLs
- **Bidirectional Mapping**: Fast O(1) lookups in both directions
- **Hit Tracking**: Built-in analytics to count URL access frequency
- **Robust Error Handling**: Custom exceptions for common edge cases
- **Thread-Safe Operations**: Synchronized for multi-threaded environments

## 📋 Usage

### Basic Usage

```java
// Create an instance of the URL shortener
URLShortenerInterface urlShortener = new XURLImpl();

// Register a long URL and get a short URL
String shortURL = urlShortener.register("https://www.example.com/very/long/path");

// Lookup a long URL using a short URL
String longURL = urlShortener.lookup(shortURL);

// Get hit count for a URL
int hits = urlShortener.getHitCount("https://www.example.com/very/long/path");
