package org.testcontainers.bookstore.cart.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CartItem {
    private String productCode;
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;
    private int quantity;

    @JsonIgnoreProperties
    public CartItem() {}

    public CartItem(
            String productCode, String productName, String productDescription, BigDecimal productPrice, int quantity) {
        this.productCode = productCode;
        this.productName = productName;
        this.productDescription = productDescription;
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

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubTotal() {
        return productPrice.multiply(new BigDecimal(quantity));
    }
}
