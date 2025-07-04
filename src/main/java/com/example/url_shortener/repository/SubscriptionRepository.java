package com.example.url_shortener.repository;

import com.example.url_shortener.model.Entity;
import com.example.url_shortener.model.enums.SubscriptionStatus; // Assuming you have this enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Entity.Subscription, Long> {
    Optional<Entity.Subscription> findByStripeSubscriptionId(String stripeId);
    List<Entity.Subscription> findByUserId(Long userId);
    List<Entity.Subscription> findByStatus(SubscriptionStatus status);
}