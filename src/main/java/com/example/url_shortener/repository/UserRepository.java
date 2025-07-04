package com.example.url_shortener.repository;

import com.example.url_shortener.model.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Mark this as a Spring component for detection
public interface UserRepository extends JpaRepository<Entity.User, Long> {
    Optional<Entity.User> findByEmail(String email);
}