package com.example.url_shortener.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;





@Entity
@Table(name = "pricing_plan")
public class PricingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price; // Consider using BigDecimal for currency values
    private int linkLimit;
    private boolean customDomains;
    private boolean apiAccess;
    private boolean analytics;
    private boolean prioritySupport;
    private boolean mostPopular;
    private String stripePriceId; // CRITICAL: This holds the Stripe Price ID for the plan

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getLinkLimit() {
        return linkLimit;
    }

    public void setLinkLimit(int linkLimit) {
        this.linkLimit = linkLimit;
    }

    public boolean isCustomDomains() {
        return customDomains;
    }

    public void setCustomDomains(boolean customDomains) {
        this.customDomains = customDomains;
    }

    public boolean isApiAccess() {
        return apiAccess;
    }

    public void setApiAccess(boolean apiAccess) {
        this.apiAccess = apiAccess;
    }

    public boolean isAnalytics() {
        return analytics;
    }

    public void setAnalytics(boolean analytics) {
        this.analytics = analytics;
    }

    public boolean isPrioritySupport() {
        return prioritySupport;
    }

    public void setPrioritySupport(boolean prioritySupport) {
        this.prioritySupport = prioritySupport;
    }

    public boolean isMostPopular() {
        return mostPopular;
    }

    public void setMostPopular(boolean mostPopular) {
        this.mostPopular = mostPopular;
    }

    public String getStripePriceId() {
        return stripePriceId;
    }

    public void setStripePriceId(String stripePriceId) {
        this.stripePriceId = stripePriceId;
    }
}
