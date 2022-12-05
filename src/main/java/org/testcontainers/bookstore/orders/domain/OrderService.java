package org.testcontainers.bookstore.orders.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.bookstore.ApplicationProperties;
import org.testcontainers.bookstore.events.OrderCreatedEvent;
import org.testcontainers.bookstore.orders.api.CreateOrderRequest;
import org.testcontainers.bookstore.orders.domain.entity.Order;
import org.testcontainers.bookstore.orders.domain.entity.OrderStatus;
import org.testcontainers.bookstore.orders.domain.model.OrderConfirmationDTO;
import org.testcontainers.bookstore.payment.domain.PaymentRequest;
import org.testcontainers.bookstore.payment.domain.PaymentResponse;
import org.testcontainers.bookstore.payment.domain.PaymentService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ApplicationProperties properties;

    public OrderService(PaymentService paymentService, OrderRepository orderRepository, OrderMapper orderMapper,
                        KafkaTemplate<String, Object> kafkaTemplate, ApplicationProperties properties) {
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    public OrderConfirmationDTO createOrder(CreateOrderRequest orderRequest) {
        Order newOrder = orderMapper.convertToEntity(orderRequest);
        Order savedOrder = this.orderRepository.save(newOrder);
        log.info("Created Order ID=" + savedOrder.getId() + ", ref_num=" + savedOrder.getOrderId());

        PaymentRequest paymentRequest = new PaymentRequest(
                orderRequest.getCardNumber(), orderRequest.getCvv(),
                orderRequest.getExpiryMonth(), orderRequest.getExpiryYear());
        PaymentResponse paymentResponse = paymentService.authorize(paymentRequest);
        if(paymentResponse.getStatus() != PaymentResponse.PaymentStatus.ACCEPTED) {
            savedOrder.setStatus(OrderStatus.ERROR);
            this.updateOrderStatus(savedOrder.getOrderId(), savedOrder.getStatus(), "Payment rejected");
        } else {
            kafkaTemplate.send(properties.newOrdersTopic(), new OrderCreatedEvent(savedOrder.getOrderId()));
            log.info("Published OrderCreatedEvent for orderId:{}", savedOrder.getOrderId());
        }
        return new OrderConfirmationDTO(savedOrder.getOrderId(), savedOrder.getStatus());
    }

    public Optional<Order> findOrderByOrderId(String orderId) {
        return this.orderRepository.findByOrderId(orderId);
    }

    public void cancelOrder(String orderId) {
        log.info("Cancel order with OrderId: {}", orderId);
        Order order = findOrderByOrderId(orderId).orElse(null);
        if (order == null) {
            throw new OrderNotFoundException(orderId);
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Order is already delivered");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public List<Order> findOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public void updateOrderStatus(String orderId, OrderStatus status, String comments) {
        Order order = orderRepository.findByOrderId(orderId).orElseThrow();
        order.setStatus(status);
        order.setComments(comments);
        orderRepository.save(order);
    }

}
