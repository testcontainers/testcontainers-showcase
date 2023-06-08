package org.testcontainers.bookstore.catalog.clients.promotions;

import java.math.BigDecimal;

public class Promotion {
    private Long id;
    private String productCode;
    private BigDecimal discount;

    public Promotion() {}

    public Promotion(Long id, String productCode, BigDecimal discount) {
        this.id = id;
        this.productCode = productCode;
        this.discount = discount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}
