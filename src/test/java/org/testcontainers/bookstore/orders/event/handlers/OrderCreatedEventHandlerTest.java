package org.testcontainers.bookstore.orders.event.handlers;

import org.testcontainers.bookstore.ApplicationProperties;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;
import org.testcontainers.bookstore.events.OrderCreatedEvent;
import org.testcontainers.bookstore.notifications.NotificationService;
import org.testcontainers.bookstore.orders.domain.OrderRepository;
import org.testcontainers.bookstore.orders.domain.entity.Order;
import org.testcontainers.bookstore.orders.domain.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class OrderCreatedEventHandlerTest extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @Autowired
    private ApplicationProperties properties;

    @MockBean
    private NotificationService notificationService;

    @Test
    void shouldIgnoreOrderCreatedEventWhenOrderNotFound() {
        kafkaTemplate.send(properties.newOrdersTopic(), new OrderCreatedEvent("non-existing-order_id"));

        await().atMost(5, SECONDS).untilAsserted(() -> {
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

        orderRepository.save(order);

        kafkaTemplate.send(properties.newOrdersTopic(), new OrderCreatedEvent(order.getOrderId()));

        await().atMost(5, SECONDS).untilAsserted(() -> {
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

        orderRepository.save(order);

        kafkaTemplate.send(properties.newOrdersTopic(), new OrderCreatedEvent(order.getOrderId()));

        await().atMost(10, SECONDS).untilAsserted(() -> {
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

        orderRepository.save(order);

        kafkaTemplate.send(properties.newOrdersTopic(), new OrderCreatedEvent(order.getOrderId()));

        await().atMost(10, SECONDS).untilAsserted(() -> {
            Order verifyingOrder = orderRepository.findByOrderId(order.getOrderId()).orElseThrow();
            assertThat(verifyingOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        });

    }


    @Test
    void shouldIgnoreOrderCreatedEventWhenOrderStatusIsNotNEW2() {
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

        orderRepository.save(order);

        kafkaTemplate.send(properties.newOrdersTopic(), new OrderCreatedEvent(order.getOrderId()));

        await().atMost(5, SECONDS).untilAsserted(() -> {
            verify(notificationService, never()).sendConfirmationNotification(any(Order.class));
        });

    }

    @Test
    void shouldHandleAndDeliverOrderForGivenOrderCreatedEvent2() {
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

        orderRepository.save(order);

        kafkaTemplate.send(properties.newOrdersTopic(), new OrderCreatedEvent(order.getOrderId()));

        await().atMost(10, SECONDS).untilAsserted(() -> {
            verify(notificationService).sendConfirmationNotification(any(Order.class));
        });

    }

    @Test
    void shouldCancelOrderWhenOrderCanNotBeDelivered2() {
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

        orderRepository.save(order);

        kafkaTemplate.send(properties.newOrdersTopic(), new OrderCreatedEvent(order.getOrderId()));

        await().atMost(10, SECONDS).untilAsserted(() -> {
            Order verifyingOrder = orderRepository.findByOrderId(order.getOrderId()).orElseThrow();
            assertThat(verifyingOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        });

    }
}