package org.testcontainers.bookstore.orders.api;

import org.testcontainers.bookstore.orders.domain.OrderNotFoundException;
import org.testcontainers.bookstore.orders.domain.OrderService;
import org.testcontainers.bookstore.orders.domain.entity.Order;
import org.testcontainers.bookstore.orders.domain.model.OrderConfirmationDTO;
import org.testcontainers.bookstore.orders.domain.model.OrderDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderConfirmationDTO placeOrder(@Valid @RequestBody CreateOrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @GetMapping(value = "/{orderId}")
    public OrderDTO getOrder(@PathVariable(value = "orderId") String orderId) {
        Order order = orderService.findOrderByOrderId(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        return new OrderDTO(order);
    }
}
