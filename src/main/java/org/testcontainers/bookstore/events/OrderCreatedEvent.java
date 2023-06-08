package org.testcontainers.bookstore.events;

public class OrderCreatedEvent {
    private String orderId;

    public OrderCreatedEvent() {}

    public OrderCreatedEvent(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
