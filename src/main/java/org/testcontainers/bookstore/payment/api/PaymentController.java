package org.testcontainers.bookstore.payment.api;

import org.testcontainers.bookstore.payment.domain.PaymentRequest;
import org.testcontainers.bookstore.payment.domain.PaymentResponse;
import org.testcontainers.bookstore.payment.domain.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/authorize")
    public PaymentResponse authorize(@Valid @RequestBody PaymentRequest paymentRequest) {
        return paymentService.authorize(paymentRequest);
    }
}
