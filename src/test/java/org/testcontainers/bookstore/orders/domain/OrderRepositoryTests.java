package org.testcontainers.bookstore.orders.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.bookstore.orders.domain.entity.Order;
import org.testcontainers.bookstore.orders.domain.entity.OrderStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {"spring.datasource.url=jdbc:tc:postgresql:15-alpine:///dbname"})
public class OrderRepositoryTests {
    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAllInBatch();
        orderRepository.saveAndFlush(buildOrder("order-1", OrderStatus.NEW));
        orderRepository.saveAndFlush(buildOrder("order-2", OrderStatus.DELIVERED));
        orderRepository.saveAndFlush(buildOrder("order-3", OrderStatus.NEW));
    }

    @Test
    void shouldGetOrderByOrderId() {
        Optional<Order> orderOptional = orderRepository.findByOrderId("order-1");
        assertThat(orderOptional).isNotEmpty();
        assertThat(orderOptional.get().getOrderId()).isEqualTo("order-1");
        assertThat(orderOptional.get().getStatus()).isEqualTo(OrderStatus.NEW);
    }

    @Test
    void shouldGetOrdersByStatus() {
        List<Order> orders = orderRepository.findByStatus(OrderStatus.NEW);
        assertThat(orders).hasSize(2);
    }

    private Order buildOrder(String orderId, OrderStatus status) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setCustomerName("Siva");
        order.setCustomerEmail("siva@gmail.com");
        order.setDeliveryAddressLine1("addr line 1");
        order.setDeliveryAddressLine2("addr line 2");
        order.setDeliveryAddressCity("Hyderabad");
        order.setDeliveryAddressState("Telangana");
        order.setDeliveryAddressZipCode("500072");
        order.setDeliveryAddressCountry("India");
        order.setStatus(status);

        return order;
    }
}
