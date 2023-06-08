package org.testcontainers.bookstore.orders.domain.model;

import org.testcontainers.bookstore.orders.domain.entity.OrderStatus;

public class OrderConfirmationDTO {
    private String orderId;
    private OrderStatus orderStatus;

    public OrderConfirmationDTO() {}

    public OrderConfirmationDTO(String orderId, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
