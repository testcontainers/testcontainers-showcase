package org.testcontainers.bookstore.events;

public class OrderCancelledEvent {
    private String orderId;

    public OrderCancelledEvent() {}

    public OrderCancelledEvent(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
