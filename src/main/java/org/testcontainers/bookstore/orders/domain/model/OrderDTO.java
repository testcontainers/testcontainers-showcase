package org.testcontainers.bookstore.orders.domain.model;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import org.testcontainers.bookstore.orders.domain.entity.Order;
import org.testcontainers.bookstore.orders.domain.entity.OrderStatus;

public class OrderDTO {
    private Long id;
    private String orderId;
    private Set<OrderItemDTO> items;
    private String customerName;
    private String customerEmail;
    private String deliveryAddressLine1;
    private String deliveryAddressLine2;
    private String deliveryAddressCity;
    private String deliveryAddressState;
    private String deliveryAddressZipCode;
    private String deliveryAddressCountry;
    private OrderStatus status;
    private String comments;

    public OrderDTO() {}

    public OrderDTO(Order order) {
        this.setId(order.getId());
        this.setOrderId(order.getOrderId());
        this.setCustomerName(order.getCustomerName());
        this.setCustomerEmail(order.getCustomerEmail());
        this.setDeliveryAddressLine1(order.getDeliveryAddressLine1());
        this.setDeliveryAddressLine2(order.getDeliveryAddressLine2());
        this.setDeliveryAddressCity(order.getDeliveryAddressCity());
        this.setDeliveryAddressState(order.getDeliveryAddressState());
        this.setDeliveryAddressZipCode(order.getDeliveryAddressZipCode());
        this.setDeliveryAddressCountry(order.getDeliveryAddressCountry());
        this.setStatus(order.getStatus());
        this.setComments(order.getComments());

        Set<OrderItemDTO> orderItemDTOs = order.getItems().stream()
                .map(item -> {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    itemDTO.setId(item.getId());
                    itemDTO.setProductCode(item.getProductCode());
                    itemDTO.setProductName(item.getProductName());
                    itemDTO.setProductPrice(item.getProductPrice());
                    itemDTO.setQuantity(itemDTO.getQuantity());
                    return itemDTO;
                })
                .collect(Collectors.toSet());
        this.setItems(orderItemDTOs);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Set<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(Set<OrderItemDTO> items) {
        this.items = items;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getDeliveryAddressLine1() {
        return deliveryAddressLine1;
    }

    public void setDeliveryAddressLine1(String deliveryAddressLine1) {
        this.deliveryAddressLine1 = deliveryAddressLine1;
    }

    public String getDeliveryAddressLine2() {
        return deliveryAddressLine2;
    }

    public void setDeliveryAddressLine2(String deliveryAddressLine2) {
        this.deliveryAddressLine2 = deliveryAddressLine2;
    }

    public String getDeliveryAddressCity() {
        return deliveryAddressCity;
    }

    public void setDeliveryAddressCity(String deliveryAddressCity) {
        this.deliveryAddressCity = deliveryAddressCity;
    }

    public String getDeliveryAddressState() {
        return deliveryAddressState;
    }

    public void setDeliveryAddressState(String deliveryAddressState) {
        this.deliveryAddressState = deliveryAddressState;
    }

    public String getDeliveryAddressZipCode() {
        return deliveryAddressZipCode;
    }

    public void setDeliveryAddressZipCode(String deliveryAddressZipCode) {
        this.deliveryAddressZipCode = deliveryAddressZipCode;
    }

    public String getDeliveryAddressCountry() {
        return deliveryAddressCountry;
    }

    public void setDeliveryAddressCountry(String deliveryAddressCountry) {
        this.deliveryAddressCountry = deliveryAddressCountry;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public static class OrderItemDTO {
        private Long id;
        private String productCode;
        private String productName;
        private BigDecimal productPrice;
        private Integer quantity;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public BigDecimal getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(BigDecimal productPrice) {
            this.productPrice = productPrice;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
