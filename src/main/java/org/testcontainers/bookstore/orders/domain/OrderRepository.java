package org.testcontainers.bookstore.orders.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.testcontainers.bookstore.orders.domain.entity.Order;
import org.testcontainers.bookstore.orders.domain.entity.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderId(String orderId);

    List<Order> findByStatus(OrderStatus status);
}
