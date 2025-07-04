package com.example.url_shortener.controller;

import com.example.url_shortener.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest; // Correct import for Jakarta EE 9+
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Added for HTTP status codes
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * REST controller for handling payment-related requests, including Stripe checkout sessions
 * and webhook events.
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Constructs a PaymentController with the given PaymentService.
     * @param paymentService The service for handling payment logic.
     */
    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Creates a Stripe checkout session for a given pricing plan and user email.
     *
     * @param planId The ID of the pricing plan selected by the user.
     * @param userEmail The email of the user initiating the checkout.
     * @param request The HttpServletRequest to construct dynamic URLs.
     * @return ResponseEntity containing the URL for the Stripe checkout page.
     */
    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(
            @RequestParam Long planId,
            @RequestParam String userEmail,
            HttpServletRequest request) {

        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        // Define success and cancel URLs for Stripe checkout.
        // {CHECKOUT_SESSION_ID} is a placeholder Stripe replaces automatically.
        String successUrl = baseUrl + "/payment/success?session_id={CHECKOUT_SESSION_ID}";
        String cancelUrl = baseUrl + "/payment/cancel";

        try {
            String sessionUrl = paymentService.createCheckoutSession(planId, userEmail, successUrl, cancelUrl);
            return ResponseEntity.ok(Collections.singletonMap("url", sessionUrl));
        } catch (RuntimeException e) {
            // Catch runtime exceptions from the service (e.g., plan/user not found, Stripe API errors)
            // and return an appropriate error response.
            System.err.println("Error creating checkout session: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * Endpoint for Stripe webhook events. This method receives and processes
     * events sent by Stripe (e.g., successful payments, subscription updates).
     *
     * @param payload The raw JSON payload of the webhook event.
     * @param sigHeader The Stripe-Signature header for webhook verification.
     * @return ResponseEntity with no content if the webhook is processed successfully.
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            paymentService.handleStripeWebhook(payload, sigHeader);
            return ResponseEntity.ok().build(); // 200 OK for successful webhook processing
        } catch (Exception e) {
            // It's crucial to return a non-2xx status code if webhook processing fails,
            // so Stripe knows to retry the event.
            System.err.println("Error processing Stripe webhook: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
