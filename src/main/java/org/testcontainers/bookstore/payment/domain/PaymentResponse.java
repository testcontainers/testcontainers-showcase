package org.testcontainers.bookstore.payment.domain;

public class PaymentResponse {
    private PaymentStatus status;

    public PaymentResponse() {}

    public PaymentResponse(PaymentStatus status) {
        this.status = status;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public enum PaymentStatus {
        ACCEPTED,
        REJECTED
    }
}
