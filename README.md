# XURL - URL Shortener

A Java-based URL shortening utility that converts long URLs into short, manageable codes for easier sharing and tracking. XURL provides efficient bidirectional mapping between original URLs and their shortened versions.

## 🚀 Features

- **URL Shortening**: Generate compact 9-character URLs from long addresses
- **Custom URLs**: Support for user-defined custom short URLs
- **Bidirectional Mapping**: Fast O(1) lookups in both directions
- **Hit Tracking**: Built-in analytics to count URL access frequency
- **Robust Error Handling**: Custom exceptions for common edge cases
- **Thread-Safe Operations**: Synchronized for multi-threaded environments

## 📋 Core Components
## URLShortenerInterface
Interface defining the contract for URL shortening operations:

- **register(String longURL)**: Generate random short URL
- **register(String longURL, String customShortURL)**: Register custom short URL
- **lookup(String shortURL)**: Retrieve original long URL
- **delete(String longURL)**: Remove URL mapping
- **getHitCount(String longURL)**: Retrieve access statistics

## XURLImpl
Concrete implementation of the URL shortener with:

HashMap-based storage for O(1) lookups
Random 9-character code generation
Hit count tracking
Error handling

## Exception Handling

- **URLAlreadyExistsException**: Thrown when attempting to map already registered URLs
- **URLNotFoundException:** Thrown when looking up non-existent URLs

## 🔧 Implementation Details

Uses three HashMap structures for bidirectional mapping and hit counting
Employs alphanumeric character set (a-z, A-Z, 0-9) for short URL generation
Guarantees uniqueness through conflict detection

## 📊 Demo Usage
javaCopy// Creating a URL shortener instance
URLShortenerInterface urlShortener = new XURLImpl();

// Register a long URL and get a short URL
String longURL = "https://www.example.com/very/long/path/to/some/resource";
String shortURL = urlShortener.register(longURL);
System.out.println("Generated short URL: " + shortURL);

// Register with custom short URL
urlShortener.register("https://www.example.com/another/path", "customURL");

// Look up the original URL
String originalURL = urlShortener.lookup(shortURL);
System.out.println("Original URL: " + originalURL);

// Check hit count
int hits = urlShortener.getHitCount(longURL);
System.out.println("Hit count: " + hits);
## 🧠 Learning Outcomes
Through this project, I developed skills in:

Java interface and implementation
Data structure optimization
Exception handling
Test-driven development
Object-oriented design principles

## 🧪 Testing
The implementation is verified through comprehensive test cases that cover:

Random and custom URL registration
URL lookups and hit count tracking
Error handling for duplicate URLs
Edge cases and boundary testing

## 📝 License
This project is open-source and available under the MIT License.
