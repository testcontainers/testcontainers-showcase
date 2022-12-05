package org.testcontainers.bookstore.orders.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.testcontainers.bookstore.orders.domain.CreateOrderHandler;
import org.testcontainers.bookstore.orders.domain.OrderNotFoundException;
import org.testcontainers.bookstore.orders.domain.OrderService;
import org.testcontainers.bookstore.orders.domain.entity.Order;
import org.testcontainers.bookstore.orders.domain.model.OrderConfirmationDTO;
import org.testcontainers.bookstore.orders.domain.model.OrderDTO;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderHandler createOrderHandler;
    private final OrderService orderService;

    public OrderController(CreateOrderHandler createOrderHandler, OrderService orderService) {
        this.createOrderHandler = createOrderHandler;
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderConfirmationDTO placeOrder(@Valid @RequestBody CreateOrderRequest orderRequest) {
        return createOrderHandler.createOrder(orderRequest);
    }

    @GetMapping(value = "/{orderId}")
    public OrderDTO getOrder(@PathVariable(value = "orderId") String orderId) {
        Order order = orderService.findOrderByOrderId(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        return new OrderDTO(order);
    }
}
