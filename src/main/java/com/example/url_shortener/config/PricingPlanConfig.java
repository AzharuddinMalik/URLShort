package com.example.url_shortener.config;

import com.example.url_shortener.model.PricingPlan;
import com.example.url_shortener.repository.PricingPlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class PricingPlanConfig {

    @Bean
    @Order(1) // Ensure this runs after table creation
    CommandLineRunner initPricingPlans(PricingPlanRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                // Free Plan
                PricingPlan freePlan = new PricingPlan();
                freePlan.setName("Fuck You");
                freePlan.setPrice(0);
                freePlan.setLinkLimit(100);
                freePlan.setCustomDomains(false);
                freePlan.setApiAccess(false);
                freePlan.setAnalytics(false);
                freePlan.setPrioritySupport(false);
                freePlan.setMostPopular(false);
                repository.save(freePlan);

                // Pro Plan
                PricingPlan proPlan = new PricingPlan();
                proPlan.setName("Pro");
                proPlan.setPrice(9);
                proPlan.setLinkLimit(1000);
                proPlan.setCustomDomains(true);
                proPlan.setApiAccess(true);
                proPlan.setAnalytics(true);
                proPlan.setPrioritySupport(true);
                proPlan.setMostPopular(true);
                repository.save(proPlan);

                // Business Plan
                PricingPlan businessPlan = new PricingPlan();
                businessPlan.setName("Business");
                businessPlan.setPrice(29);
                businessPlan.setLinkLimit(10000);
                businessPlan.setCustomDomains(true);
                businessPlan.setApiAccess(true);
                businessPlan.setAnalytics(true);
                businessPlan.setPrioritySupport(true);
                businessPlan.setMostPopular(false);
                repository.save(businessPlan);
            }
        };
    }
}