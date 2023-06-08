package org.testcontainers.bookstore.orders.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.testcontainers.bookstore.ApplicationProperties;
import org.testcontainers.bookstore.events.OrderCreatedEvent;
import org.testcontainers.bookstore.orders.api.CreateOrderRequest;
import org.testcontainers.bookstore.orders.domain.entity.OrderStatus;
import org.testcontainers.bookstore.orders.domain.model.OrderConfirmationDTO;

@Component
public class CreateOrderHandler {
    private static final Logger log = LoggerFactory.getLogger(CreateOrderHandler.class);

    private final OrderService orderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ApplicationProperties properties;

    public CreateOrderHandler(
            OrderService orderService, KafkaTemplate<String, Object> kafkaTemplate, ApplicationProperties properties) {
        this.orderService = orderService;
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    public OrderConfirmationDTO createOrder(CreateOrderRequest orderRequest) {
        OrderConfirmationDTO orderConfirmationDTO = orderService.createOrder(orderRequest);
        if (orderConfirmationDTO.getOrderStatus() == OrderStatus.NEW) {
            kafkaTemplate.send(properties.newOrdersTopic(), new OrderCreatedEvent(orderConfirmationDTO.getOrderId()));
            log.info("Published OrderCreatedEvent for orderId:{}", orderConfirmationDTO.getOrderId());
        }
        return orderConfirmationDTO;
    }
}
