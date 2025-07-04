package com.example.url_shortener.service;

// Correct imports for nested entities
import com.example.url_shortener.model.Entity;
import com.example.url_shortener.model.Entity.Subscription;
import com.example.url_shortener.model.Entity.Payment;
import com.example.url_shortener.model.PricingPlan;

// Import enums from the correct package (create these files if they don't exist)
import com.example.url_shortener.model.enums.PaymentStatus;
import com.example.url_shortener.model.enums.SubscriptionStatus;

// Import the top-level repository interfaces
import com.example.url_shortener.repository.UserRepository;
import com.example.url_shortener.repository.SubscriptionRepository;
import com.example.url_shortener.repository.PaymentRepository;
import com.example.url_shortener.repository.PricingPlanRepository;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service class for handling all Stripe payment and subscription related logic.
 * This includes creating checkout sessions, processing webhooks, and updating
 * subscription and payment records in the database.
 */
@Service
public class PaymentService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    // Repositories for interacting with the database - now top-level interfaces
    private final PricingPlanRepository pricingPlanRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;

    /**
     * Constructor for PaymentService, injecting necessary repositories.
     *
     * @param pricingPlanRepository Repository for PricingPlan entities.
     * @param userRepository Repository for User entities.
     * @param subscriptionRepository Repository for Subscription entities.
     * @param paymentRepository Repository for Payment entities.
     */
    @Autowired
    public PaymentService(PricingPlanRepository pricingPlanRepository,
                          UserRepository userRepository, // Now a top-level interface
                          SubscriptionRepository subscriptionRepository, // Now a top-level interface
                          PaymentRepository paymentRepository) { // Now a top-level interface
        this.pricingPlanRepository = pricingPlanRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.paymentRepository = paymentRepository;
    }

    /**
     * Creates a Stripe Checkout Session. This method is called from the frontend
     * to initiate the payment process, redirecting the user to a Stripe-hosted page.
     *
     * @param planId The ID of the pricing plan selected by the user.
     * @param userEmail The email of the user initiating the checkout.
     * @param successUrl The URL Stripe redirects to upon successful payment.
     * @param cancelUrl The URL Stripe redirects to if the user cancels.
     * @return The URL of the Stripe Checkout Session.
     * @throws RuntimeException if the plan or user is not found, or if there's a Stripe API error.
     */
    public String createCheckoutSession(Long planId, String userEmail, String successUrl, String cancelUrl) {
        Stripe.apiKey = stripeApiKey;

        PricingPlan plan = pricingPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Pricing plan not found with ID: " + planId));

        Entity.User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .setCustomerEmail(userEmail)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice(plan.getStripePriceId())
                                    .setQuantity(1L)
                                    .build())
                    .build();

            Session session = Session.create(params);
            return session.getUrl();
        } catch (StripeException e) {
            System.err.println("StripeException during createCheckoutSession: " + e.getMessage());
            throw new RuntimeException("Error creating Stripe checkout session: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Unexpected error during createCheckoutSession: " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Handles incoming Stripe webhook events.
     *
     * @param payload The raw JSON payload received from Stripe.
     * @param sigHeader The value of the 'Stripe-Signature' header, used for verification.
     * @throws RuntimeException if webhook verification fails or an event cannot be processed.
     */
    @Transactional
    public void handleStripeWebhook(String payload, String sigHeader) {
        Stripe.apiKey = stripeApiKey;

        String webhookSecret = System.getenv("STRIPE_WEBHOOK_SECRET");
        if (webhookSecret == null) {
            System.err.println("STRIPE_WEBHOOK_SECRET environment variable not set. Webhook verification will fail.");
            throw new RuntimeException("Stripe webhook secret not configured.");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            System.out.println("Received Stripe event: " + event.getType());
        } catch (StripeException e) {
            System.err.println("Webhook signature verification failed: " + e.getMessage());
            throw new RuntimeException("Stripe webhook verification failed", e);
        } catch (Exception e) {
            System.err.println("Unexpected error during webhook construction: " + e.getMessage());
            throw new RuntimeException("Error constructing webhook event", e);
        }

        switch (event.getType()) {
            case "checkout.session.completed":
                handleCheckoutSessionCompleted(event);
                break;
            case "invoice.paid":
                handleInvoicePaid(event);
                break;
            case "customer.subscription.deleted":
                handleSubscriptionDeleted(event);
                break;
            default:
                System.out.println("Unhandled event type: " + event.getType());
                break;
        }
    }

    private void handleCheckoutSessionCompleted(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new RuntimeException("Failed to deserialize Checkout Session object from event."));

        String customerEmail = session.getCustomerEmail();
        String stripeSubscriptionId = session.getSubscription();

        Entity.User user = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + customerEmail));

        if (subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId).isPresent()) {
            System.out.println("Subscription with Stripe ID " + stripeSubscriptionId + " already exists. Skipping creation.");
            return;
        }

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setStripeSubscriptionId(stripeSubscriptionId);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDateTime.now());
        // TODO: Link the PricingPlan to the subscription. You might need to add metadata to the Stripe Checkout Session
        // e.g., SessionCreateParams.builder().addMetadata("plan_id", plan.getId().toString())
        // And then retrieve it here: Long dbPlanId = Long.parseLong(session.getMetadata().get("plan_id"));
        // PricingPlan dbPlan = pricingPlanRepository.findById(dbPlanId).orElse(null);
        // subscription.setPlan(dbPlan); // Assuming you add setPlan to Subscription model
        subscriptionRepository.save(subscription);
        System.out.println("New subscription created for user: " + customerEmail + " with Stripe ID: " + stripeSubscriptionId);
    }

    private void handleInvoicePaid(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new RuntimeException("Failed to deserialize Invoice object from event."));

        String stripeSubscriptionId = invoice.getSubscription();

        Subscription subscription = subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found for Stripe Subscription ID: " + stripeSubscriptionId));

        Payment payment = new Payment();
        payment.setSubscription(subscription);
        payment.setAmount(BigDecimal.valueOf(invoice.getAmountPaid() / 100.0));
        payment.setCurrency(invoice.getCurrency().toUpperCase());
        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setStripePaymentId(invoice.getPaymentIntent());
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        System.out.println("New payment recorded for subscription " + stripeSubscriptionId + ". Amount: " + payment.getAmount() + " " + payment.getCurrency());
    }

    private void handleSubscriptionDeleted(Event event) {
        com.stripe.model.Subscription stripeSubscription =
                (com.stripe.model.Subscription) event.getDataObjectDeserializer().getObject()
                        .orElseThrow(() -> new RuntimeException("Failed to deserialize Stripe Subscription object from event."));

        Subscription subscription = subscriptionRepository.findByStripeSubscriptionId(stripeSubscription.getId())
                .orElseThrow(() -> new RuntimeException("Subscription not found for Stripe Subscription ID: " + stripeSubscription.getId()));

        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscription.setEndDate(LocalDateTime.now());
        subscriptionRepository.save(subscription);
        System.out.println("Subscription with Stripe ID " + stripeSubscription.getId() + " marked as CANCELED.");
    }
}