package org.testcontainers.bookstore.orders.api;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Set;

public class CreateOrderRequest {

    @NotEmpty(message = "Items cannot be empty.")
    private Set<LineItem> items;

    @NotBlank(message = "Customer Name is required")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email
    private String customerEmail;

    @NotBlank(message = "DeliveryAddress Line1 is required")
    private String deliveryAddressLine1;

    private String deliveryAddressLine2;

    @NotBlank(message = "DeliveryAddress City is required")
    private String deliveryAddressCity;

    @NotBlank(message = "DeliveryAddress State is required")
    private String deliveryAddressState;

    @NotBlank(message = "DeliveryAddress ZipCode is required")
    private String deliveryAddressZipCode;

    @NotBlank(message = "DeliveryAddress Country is required")
    private String deliveryAddressCountry;

    @NotBlank(message = "Card Number is required")
    private String cardNumber;

    @NotBlank(message = "CVV is required")
    private String cvv;

    @NotNull private Integer expiryMonth;

    @NotNull private Integer expiryYear;

    public Set<LineItem> getItems() {
        return items;
    }

    public void setItems(Set<LineItem> items) {
        this.items = items;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getDeliveryAddressLine1() {
        return deliveryAddressLine1;
    }

    public void setDeliveryAddressLine1(String deliveryAddressLine1) {
        this.deliveryAddressLine1 = deliveryAddressLine1;
    }

    public String getDeliveryAddressLine2() {
        return deliveryAddressLine2;
    }

    public void setDeliveryAddressLine2(String deliveryAddressLine2) {
        this.deliveryAddressLine2 = deliveryAddressLine2;
    }

    public String getDeliveryAddressCity() {
        return deliveryAddressCity;
    }

    public void setDeliveryAddressCity(String deliveryAddressCity) {
        this.deliveryAddressCity = deliveryAddressCity;
    }

    public String getDeliveryAddressState() {
        return deliveryAddressState;
    }

    public void setDeliveryAddressState(String deliveryAddressState) {
        this.deliveryAddressState = deliveryAddressState;
    }

    public String getDeliveryAddressZipCode() {
        return deliveryAddressZipCode;
    }

    public void setDeliveryAddressZipCode(String deliveryAddressZipCode) {
        this.deliveryAddressZipCode = deliveryAddressZipCode;
    }

    public String getDeliveryAddressCountry() {
        return deliveryAddressCountry;
    }

    public void setDeliveryAddressCountry(String deliveryAddressCountry) {
        this.deliveryAddressCountry = deliveryAddressCountry;
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

    public static class LineItem {
        @NotBlank(message = "productCode is required")
        private String productCode;

        private String productName;
        private BigDecimal productPrice;

        @NotNull @Min(1)
        private Integer quantity;

        public LineItem() {}

        public LineItem(String productCode, String productName, BigDecimal productPrice, Integer quantity) {
            this.productCode = productCode;
            this.productName = productName;
            this.productPrice = productPrice;
            this.quantity = quantity;
        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public BigDecimal getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(BigDecimal productPrice) {
            this.productPrice = productPrice;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
