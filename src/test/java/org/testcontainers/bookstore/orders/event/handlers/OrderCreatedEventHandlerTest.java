package org.testcontainers.bookstore.orders.event.handlers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
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
import org.testcontainers.bookstore.events.OrderCreatedEvent;
import org.testcontainers.bookstore.orders.domain.OrderRepository;
import org.testcontainers.bookstore.orders.domain.entity.Order;
import org.testcontainers.bookstore.orders.domain.entity.OrderStatus;

public class OrderCreatedEventHandlerTest extends AbstractIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventHandlerTest.class);

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
    void shouldIgnoreOrderCreatedEventWhenOrderNotFound() {
        kafkaTemplate.send(properties.newOrdersTopic(), new OrderCreatedEvent("non-existing-order_id"));

        await().pollInterval(Duration.ofSeconds(5)).atMost(30, SECONDS).untilAsserted(() -> {
            verify(notificationService, never()).sendConfirmationNotification(any(Order.class));
        });
    }

    @Test
    void shouldIgnoreOrderCreatedEventWhenOrderStatusIsNotNEW() {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.CANCELLED);
        order.setCustomerName("Siva");
        order.setCustomerEmail("siva@gmail.com");
        order.setDeliveryAddressLine1("addr line 1");
        order.setDeliveryAddressLine2("addr line 2");
        order.setDeliveryAddressCity("Hyderabad");
        order.setDeliveryAddressState("Telangana");
        order.setDeliveryAddressZipCode("500072");
        order.setDeliveryAddressCountry("India");

        orderRepository.saveAndFlush(order);
        log.info("Created OrderId: {}", order.getOrderId());
        kafkaTemplate.send(properties.newOrdersTopic(), new OrderCreatedEvent(order.getOrderId()));

        await().pollInterval(Duration.ofSeconds(5)).atMost(30, SECONDS).untilAsserted(() -> {
            verify(notificationService, never()).sendConfirmationNotification(any(Order.class));
        });
    }

    @Test
    void shouldHandleAndDeliverOrderForGivenOrderCreatedEvent() {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.NEW);
        order.setCustomerName("Siva");
        order.setCustomerEmail("siva@gmail.com");
        order.setDeliveryAddressLine1("addr line 1");
        order.setDeliveryAddressLine2("addr line 2");
        order.setDeliveryAddressCity("Hyderabad");
        order.setDeliveryAddressState("Telangana");
        order.setDeliveryAddressZipCode("500072");
        order.setDeliveryAddressCountry("India");

        orderRepository.saveAndFlush(order);

        kafkaTemplate.send(properties.newOrdersTopic(), new OrderCreatedEvent(order.getOrderId()));

        await().pollInterval(Duration.ofSeconds(5)).atMost(30, SECONDS).untilAsserted(() -> {
            verify(notificationService).sendConfirmationNotification(any(Order.class));
        });
    }

    @Test
    void shouldCancelOrderWhenOrderCanNotBeDelivered() {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.NEW);
        order.setCustomerName("Siva");
        order.setCustomerEmail("siva@gmail.com");
        order.setDeliveryAddressLine1("addr line 1");
        order.setDeliveryAddressLine2("addr line 2");
        order.setDeliveryAddressCity("Japan");
        order.setDeliveryAddressState("Japan");
        order.setDeliveryAddressZipCode("500072");
        order.setDeliveryAddressCountry("Japan");

        orderRepository.saveAndFlush(order);

        kafkaTemplate.send(properties.newOrdersTopic(), new OrderCreatedEvent(order.getOrderId()));

        await().pollInterval(Duration.ofSeconds(5)).atMost(30, SECONDS).untilAsserted(() -> {
            Order verifyingOrder =
                    orderRepository.findByOrderId(order.getOrderId()).orElseThrow();
            assertThat(verifyingOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        });
    }
}
