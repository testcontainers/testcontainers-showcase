package org.testcontainers.bookstore.payment.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {
    @NotBlank(message = "cardNumber is required")
    private String cardNumber;

    @NotBlank(message = "cvv is required")
    private String cvv;

    @NotNull(message = "expiryMonth is required") private Integer expiryMonth;

    @NotNull(message = "expiryYear is required") private Integer expiryYear;

    public PaymentRequest() {}

    public PaymentRequest(String cardNumber, String cvv, Integer expiryMonth, Integer expiryYear) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Integer getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(Integer expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public Integer getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(Integer expiryYear) {
        this.expiryYear = expiryYear;
    }
}
