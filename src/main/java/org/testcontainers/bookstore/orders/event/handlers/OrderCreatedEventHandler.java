package org.testcontainers.bookstore.orders.event.handlers;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.testcontainers.bookstore.ApplicationProperties;
import org.testcontainers.bookstore.events.OrderCancelledEvent;
import org.testcontainers.bookstore.events.OrderCreatedEvent;
import org.testcontainers.bookstore.events.OrderDeliveredEvent;
import org.testcontainers.bookstore.notifications.NotificationService;
import org.testcontainers.bookstore.orders.domain.OrderService;
import org.testcontainers.bookstore.orders.domain.entity.Order;
import org.testcontainers.bookstore.orders.domain.entity.OrderStatus;

@Component
public class OrderCreatedEventHandler {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventHandler.class);
    private static final List<String> DELIVERY_ALLOWED_COUNTRIES = List.of("INDIA", "USA", "GERMANY", "UK");

    private final OrderService orderService;
    private final NotificationService notificationService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ApplicationProperties properties;

    public OrderCreatedEventHandler(
            OrderService orderService,
            NotificationService notificationService,
            KafkaTemplate<String, Object> kafkaTemplate,
            ApplicationProperties properties) {
        this.orderService = orderService;
        this.notificationService = notificationService;
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    @KafkaListener(topics = "${app.new-orders-topic}")
    public void handle(OrderCreatedEvent event) {
        log.info("Received a OrderCreatedEvent with orderId:{}: ", event.getOrderId());
        Order order = orderService.findOrderByOrderId(event.getOrderId()).orElse(null);
        if (order == null) {
            log.warn("Received a OrderCreatedEvent with invalid orderId:{}: ", event.getOrderId());
            return;
        }
        if (order.getStatus() != OrderStatus.NEW) {
            log.warn(
                    "Received a OrderCreatedEvent with invalid status:{} for orderId:{}: ",
                    order.getStatus(),
                    event.getOrderId());
            orderService.updateOrderStatus(
                    order.getOrderId(),
                    OrderStatus.ERROR,
                    "Received a new order with invalid status=" + order.getStatus());
            return;
        }
        notificationService.sendConfirmationNotification(order);
        // logic to process order delivery
        if (canBeDelivered(order)) {
            orderService.updateOrderStatus(order.getOrderId(), OrderStatus.DELIVERED, null);
            kafkaTemplate.send(properties.deliveredOrdersTopic(), new OrderDeliveredEvent(order.getOrderId()));
        } else {
            orderService.updateOrderStatus(
                    order.getOrderId(), OrderStatus.CANCELLED, "Can't deliver to your delivery address");
            kafkaTemplate.send(properties.cancelledOrdersTopic(), new OrderCancelledEvent(order.getOrderId()));
        }
    }

    private boolean canBeDelivered(Order order) {
        return DELIVERY_ALLOWED_COUNTRIES.contains(
                order.getDeliveryAddressCountry().toUpperCase());
    }
}
