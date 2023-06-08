package org.testcontainers.bookstore.events;

public class OrderDeliveredEvent {
    private String orderId;

    public OrderDeliveredEvent() {}

    public OrderDeliveredEvent(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
