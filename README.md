# URL Shortener

A simple URL shortener application built with Spring Boot, Spring Data JPA, and MySQL. This project demonstrates how to create a REST API to generate and resolve shortened URLs.

## Table of Contents

- [URL Shortener](#url-shortener)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
  - [Tech Stack](#tech-stack)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
    - [MySQL Setup](#mysql-setup)
  - [Usage](#usage)
  - [API Endpoints](#api-endpoints)
    - [Shorten URL](#shorten-url)
    - [Redirect URL](#redirect-url)

## Features

- Generate short URLs for long URLs.
- Redirect short URLs to their corresponding long URLs.
- Persist URL mappings in a MySQL database.
- Auto-create database schema with Hibernate.
- LiveReload support during development with Spring Boot DevTools.

## Tech Stack

- **Spring Boot** - Application framework.
- **Spring Data JPA** - Database access and ORM.
- **MySQL** - Relational database.
- **HikariCP** - Connection pooling.
- **Apache Commons Lang** - Utility functions (for generating random strings).
- **Maven** - Build and dependency management.

## Prerequisites

- **Java 17 or later** (Java 22 was used in development)
- **Maven** installed on your system.
- **MySQL** server running (or a Docker container with MySQL).
- An IDE such as IntelliJ IDEA or VS Code (optional).

## Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/url-shortener.git
   cd url-shortener

2. **Generate the project (if starting from scratch)::**
- Visit [Spring Initializr](https://start.spring.io/).

- Fill in the Project Details:
  - **Group:** `com.example`
  - **Artifact:** `url-shortener`
  - **Dependencies:** Spring Web, Spring Data JPA, MySQL Driver, Lombok, DevTools.

- Download and extract the project, then replace the source with this repository's structure if needed.

3. **Build the project using Maven:**
   ```bash
   mvn clean install

## Configuration

### MySQL Setup

- **Using MySQL Docker Container:**

    If you're running MySQL in a Docker container, ensure it is running and mapped to the host port 3306:

    ```bash
    docker run --name mysql-container -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql

- **Database Configuration:**
  Update the src/main/resources/application.properties file with your database details:
  - properties
    ```properties
    spring.application.name=url-shortener
    spring.datasource.url=jdbc:mysql://localhost:3306/url_shortener?createDatabaseIfNotExist=true
    spring.datasource.username=root
    spring.datasource.password=root
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    
    # Hibernate Configuration
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    
    # Optional: Disable open-in-view in production
    spring.jpa.open-in-view=false

## Usage
1. **Run the application:**
    ```bash
    mvn spring-boot:run
The application will start on port 8080.
    
2. **Access the site: Open your web browser and navigate to:**
    ```bash
    http://localhost:8080/
*(Ensure you have a homepage or default endpoint configured.)*

## API Endpoints

### Shorten URL
- **Endpoint:** `POST /api/url/shorten`  
- **Description:** Generates a short URL for a given long URL.

- **Usage Example:**
    ```bash
    curl -X POST "http://localhost:8080/api/url/shorten?longUrl=https://example.com"
- **Response:**
  ```json
  {
    "shortUrl": "http://localhost:8080/api/url/abc123"
  }

### Redirect URL

- **Endpoint:** `GET /api/url/{shortUrl}`
- **Description:** Redirects the short URL to the original long URL.
- **Usage Example:** Navigate in your browser to:
    ```bash
    http://localhost:8080/api/url/abc123
    

