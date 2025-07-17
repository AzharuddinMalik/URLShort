package com.example.url_shortener.model;

import com.example.url_shortener.model.enums.PaymentStatus;
import com.example.url_shortener.model.enums.SubscriptionStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// This class acts as a container for your static nested entity classes.
// Note: While functional, a more common Spring Boot approach is to have each entity in its own .java file.
public class Entity {

    @jakarta.persistence.Entity
    @Table(name = "users")
    public static class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY) // Compatible with PostgreSQL's SERIAL/BIGSERIAL
        private Long id;

        @Column(nullable = false, unique = true)
        private String email;

        @Column(nullable = false)
        private String password;

        @Column(nullable = false)
        private LocalDateTime createdAt; // Maps to TIMESTAMP WITHOUT TIME ZONE in PostgreSQL

        // Getters and setters (omitted for brevity, assume they are correct)
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    @jakarta.persistence.Entity
    @Table(name = "subscriptions")
    public static class Subscription {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY) // Compatible with PostgreSQL's SERIAL/BIGSERIAL
        private Long id;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @ManyToOne
        @JoinColumn(name = "plan_id", nullable = false)
        private PricingPlan plan;

        @Enumerated(EnumType.STRING) // Stores enum name as VARCHAR, compatible
        private SubscriptionStatus status;

        private LocalDateTime startDate; // Maps to TIMESTAMP WITHOUT TIME ZONE
        private LocalDateTime endDate;   // Maps to TIMESTAMP WITHOUT TIME ZONE
        @Column(unique = true, nullable = false)
        private String stripeSubscriptionId;

        // Getters and setters (omitted for brevity, assume they are correct)
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
        public PricingPlan getPlan() { return plan; }
        public void setPlan(PricingPlan plan) { this.plan = plan; }
        public SubscriptionStatus getStatus() { return status; }
        public void setStatus(SubscriptionStatus status) { this.status = status; }
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
        public String getStripeSubscriptionId() { return stripeSubscriptionId; }
        public void setStripeSubscriptionId(String stripeSubscriptionId) { this.stripeSubscriptionId = stripeSubscriptionId; }
    }

    @jakarta.persistence.Entity
    @Table(name = "payments")
    public static class Payment {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY) // Compatible with PostgreSQL's SERIAL/BIGSERIAL
        private Long id;

        @ManyToOne
        @JoinColumn(name = "subscription_id", nullable = false)
        private Subscription subscription;

        @Column(nullable = false)
        private BigDecimal amount; // Maps to NUMERIC or DECIMAL in PostgreSQL
        @Column(nullable = false)
        private String currency;

        @Enumerated(EnumType.STRING) // Stores enum name as VARCHAR, compatible
        private PaymentStatus status;

        @Column(unique = true)
        private String stripePaymentId;
        @Column(nullable = false)
        private LocalDateTime createdAt; // Maps to TIMESTAMP WITHOUT TIME ZONE

        // Getters and setters (omitted for brevity, assume they are correct)
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Subscription getSubscription() { return subscription; }
        public void setSubscription(Subscription subscription) { this.subscription = subscription; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public PaymentStatus getStatus() { return status; }
        public void setStatus(PaymentStatus status) { this.status = status; }
        public String getStripePaymentId() { return stripePaymentId; }
        public void setStripePaymentId(String stripePaymentId) { this.stripePaymentId = stripePaymentId; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
