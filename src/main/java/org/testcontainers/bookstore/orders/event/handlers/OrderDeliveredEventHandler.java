package org.testcontainers.bookstore.orders.event.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.testcontainers.bookstore.events.OrderDeliveredEvent;
import org.testcontainers.bookstore.notifications.NotificationService;
import org.testcontainers.bookstore.orders.domain.OrderService;
import org.testcontainers.bookstore.orders.domain.entity.Order;

@Component
public class OrderDeliveredEventHandler {
    private static final Logger log = LoggerFactory.getLogger(OrderDeliveredEventHandler.class);

    private final OrderService orderService;
    private final NotificationService notificationService;

    public OrderDeliveredEventHandler(OrderService orderService, NotificationService notificationService) {
        this.orderService = orderService;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${app.delivered-orders-topic}")
    public void handle(OrderDeliveredEvent event) {
        log.info("Received a OrderDeliveredEvent with orderId:{}: ", event.getOrderId());
        Order order = orderService.findOrderByOrderId(event.getOrderId()).orElse(null);
        if (order == null) {
            log.info("Received invalid OrderDeliveredEvent with orderId:{}: ", event.getOrderId());
            return;
        }
        notificationService.sendDeliveredNotification(order);
    }
}
