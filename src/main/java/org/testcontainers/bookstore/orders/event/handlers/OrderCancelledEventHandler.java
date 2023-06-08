package org.testcontainers.bookstore.orders.event.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.testcontainers.bookstore.events.OrderCancelledEvent;
import org.testcontainers.bookstore.notifications.NotificationService;
import org.testcontainers.bookstore.orders.domain.OrderService;
import org.testcontainers.bookstore.orders.domain.entity.Order;

@Component
public class OrderCancelledEventHandler {
    private static final Logger log = LoggerFactory.getLogger(OrderCancelledEventHandler.class);

    private final OrderService orderService;
    private final NotificationService notificationService;

    public OrderCancelledEventHandler(OrderService orderService, NotificationService notificationService) {
        this.orderService = orderService;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${app.cancelled-orders-topic}")
    public void handle(OrderCancelledEvent event) {
        log.info("Received a OrderCancelledEvent with orderId:{}: ", event.getOrderId());
        Order order = orderService.findOrderByOrderId(event.getOrderId()).orElse(null);
        if (order == null) {
            log.info("Received invalid OrderCancelledEvent with orderId:{}: ", event.getOrderId());
            return;
        }
        notificationService.sendCancelledNotification(order);
    }
}
