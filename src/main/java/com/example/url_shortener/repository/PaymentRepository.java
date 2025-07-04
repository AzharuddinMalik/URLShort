package com.example.url_shortener.repository;

import com.example.url_shortener.model.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Entity.Payment, Long> {
    Optional<Entity.Payment> findByStripePaymentId(String stripeId);
}