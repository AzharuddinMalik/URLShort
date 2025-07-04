package com.example.url_shortener.model;

import com.example.url_shortener.model.enums.PaymentStatus;
import com.example.url_shortener.model.enums.SubscriptionStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Import the enums you've defined, assuming they are in the 'enums' package


// This class acts as a container for your static nested entity classes.
// Note: While functional, a more common Spring Boot approach is to have each entity in its own .java file.
public class Entity {

    @jakarta.persistence.Entity // Correct JPA Entity annotation
    @Table(name = "users")
    public static class User { // Make User static for easier referencing within the outer Entity class
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY) // Corrected 'strategyz' to 'strategy'
        private Long id;

        @Column(nullable = false, unique = true)
        private String email;

        @Column(nullable = false)
        private String password;

        @Column(nullable = false)
        private LocalDateTime createdAt;

        // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    @jakarta.persistence.Entity // Correct JPA Entity annotation
    @Table(name = "subscriptions")
    public static class Subscription { // Make Subscription static
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY) // Corrected 'strategyz' to 'strategy'
        private Long id;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user; // Reference the nested User class

        @ManyToOne
        @JoinColumn(name = "plan_id", nullable = false)
        private PricingPlan plan; // PricingPlan is a top-level entity, so no 'Entity.' prefix needed

        @Enumerated(EnumType.STRING)
        private SubscriptionStatus status; // Reference the imported enum

        private LocalDateTime startDate;
        private LocalDateTime endDate;
        @Column(unique = true, nullable = false) // Add unique constraint for Stripe ID
        private String stripeSubscriptionId;

        // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public PricingPlan getPlan() {
            return plan;
        }

        public void setPlan(PricingPlan plan) {
            this.plan = plan;
        }

        public SubscriptionStatus getStatus() {
            return status;
        }

        public void setStatus(SubscriptionStatus status) {
            this.status = status;
        }

        public LocalDateTime getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDateTime startDate) {
            this.startDate = startDate;
        }

        public String getStripeSubscriptionId() {
            return stripeSubscriptionId;
        }

        public void setStripeSubscriptionId(String stripeSubscriptionId) {
            this.stripeSubscriptionId = stripeSubscriptionId;
        }

        public LocalDateTime getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDateTime endDate) {
            this.endDate = endDate;
        }
    }

    @jakarta.persistence.Entity // Correct JPA Entity annotation
    @Table(name = "payments")
    public static class Payment { // Make Payment static
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "subscription_id", nullable = false)
        private Subscription subscription; // Reference the nested Subscription class

        @Column(nullable = false)
        private BigDecimal amount;
        @Column(nullable = false)
        private String currency;

        @Enumerated(EnumType.STRING)
        private PaymentStatus status; // Reference the imported enum

        @Column(unique = true) // Stripe Payment ID might be unique
        private String stripePaymentId; // Corresponds to Stripe PaymentIntent ID or Charge ID
        @Column(nullable = false)
        private LocalDateTime createdAt;

        // Getters and setters
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public String getStripePaymentId() {
            return stripePaymentId;
        }

        public void setStripePaymentId(String stripePaymentId) {
            this.stripePaymentId = stripePaymentId;
        }

        public PaymentStatus getStatus() {
            return status;
        }

        public void setStatus(PaymentStatus status) {
            this.status = status;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Subscription getSubscription() {
            return subscription;
        }

        public void setSubscription(Subscription subscription) {
            this.subscription = subscription;
        }
    }
}
