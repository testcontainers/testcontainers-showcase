package org.testcontainers.bookstore.orders.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.testcontainers.bookstore.orders.api.CreateOrderRequest;
import org.testcontainers.bookstore.orders.domain.entity.Order;
import org.testcontainers.bookstore.orders.domain.entity.OrderItem;
import org.testcontainers.bookstore.orders.domain.entity.OrderStatus;

@Component
public class OrderMapper {

    public Order convertToEntity(CreateOrderRequest orderRequest) {
        Order newOrder = new Order();
        newOrder.setOrderId(UUID.randomUUID().toString());
        newOrder.setStatus(OrderStatus.NEW);
        newOrder.setCustomerName(orderRequest.getCustomerName());
        newOrder.setCustomerEmail(orderRequest.getCustomerEmail());
        newOrder.setDeliveryAddressLine1(orderRequest.getDeliveryAddressLine1());
        newOrder.setDeliveryAddressLine2(orderRequest.getDeliveryAddressLine2());
        newOrder.setDeliveryAddressCity(orderRequest.getDeliveryAddressCity());
        newOrder.setDeliveryAddressState(orderRequest.getDeliveryAddressState());
        newOrder.setDeliveryAddressZipCode(orderRequest.getDeliveryAddressZipCode());
        newOrder.setDeliveryAddressCountry(orderRequest.getDeliveryAddressCountry());

        Set<OrderItem> orderItems = new HashSet<>();
        for (CreateOrderRequest.LineItem item : orderRequest.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductCode(item.getProductCode());
            orderItem.setProductName(item.getProductName());
            orderItem.setProductPrice(item.getProductPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setOrder(newOrder);
            orderItems.add(orderItem);
        }
        newOrder.setItems(orderItems);
        return newOrder;
    }
}
