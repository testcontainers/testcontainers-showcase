package org.testcontainers.bookstore.orders.event.handlers;

import org.testcontainers.bookstore.ApplicationProperties;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;
import org.testcontainers.bookstore.events.OrderDeliveredEvent;
import org.testcontainers.bookstore.notifications.NotificationService;
import org.testcontainers.bookstore.orders.domain.OrderRepository;
import org.testcontainers.bookstore.orders.domain.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class OrderDeliveredEventHandlerTest extends AbstractIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @Autowired
    private ApplicationProperties properties;

    @MockBean
    private NotificationService notificationService;

    @Test
    void shouldHandleOrderDeliveredEvent() {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setCustomerName("Siva");
        order.setCustomerEmail("siva@gmail.com");
        order.setDeliveryAddressLine1("addr line 1");
        order.setDeliveryAddressLine2("addr line 2");
        order.setDeliveryAddressCity("Hyderabad");
        order.setDeliveryAddressState("Telangana");
        order.setDeliveryAddressZipCode("500072");
        order.setDeliveryAddressCountry("India");

        orderRepository.save(order);

        kafkaTemplate.send(properties.deliveredOrdersTopic(), new OrderDeliveredEvent(order.getOrderId()));

        await().atMost(30, SECONDS).untilAsserted(() -> {
            verify(notificationService).sendDeliveredNotification(any(Order.class));
        });

    }

    @Test
    void shouldIgnoreOrderDeliveredEventWhenOrderNotFound() {
        kafkaTemplate.send(properties.deliveredOrdersTopic(), new OrderDeliveredEvent("non-existing-order_id"));

        await().atMost(5, SECONDS).untilAsserted(() -> {
            verify(notificationService, never()).sendDeliveredNotification(any(Order.class));
        });

    }
}