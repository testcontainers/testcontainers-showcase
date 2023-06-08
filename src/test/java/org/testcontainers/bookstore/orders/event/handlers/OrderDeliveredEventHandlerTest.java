package org.testcontainers.bookstore.orders.event.handlers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.bookstore.ApplicationProperties;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;
import org.testcontainers.bookstore.events.OrderDeliveredEvent;
import org.testcontainers.bookstore.orders.domain.OrderRepository;
import org.testcontainers.bookstore.orders.domain.entity.Order;

public class OrderDeliveredEventHandlerTest extends AbstractIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(OrderDeliveredEventHandlerTest.class);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ApplicationProperties properties;

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

        orderRepository.saveAndFlush(order);
        log.info("Delivered OrderId: {}", order.getOrderId());
        kafkaTemplate.send(properties.deliveredOrdersTopic(), new OrderDeliveredEvent(order.getOrderId()));

        await().pollInterval(Duration.ofSeconds(5)).atMost(30, SECONDS).untilAsserted(() -> {
            verify(notificationService).sendDeliveredNotification(any(Order.class));
        });
    }

    @Test
    void shouldIgnoreOrderDeliveredEventWhenOrderNotFound() {
        String orderId = UUID.randomUUID().toString();
        log.info("Delivered OrderId: {}", orderId);
        kafkaTemplate.send(properties.deliveredOrdersTopic(), new OrderDeliveredEvent(orderId));

        await().pollInterval(Duration.ofSeconds(5)).atMost(30, SECONDS).untilAsserted(() -> {
            verify(notificationService, never()).sendDeliveredNotification(any(Order.class));
        });
    }
}
