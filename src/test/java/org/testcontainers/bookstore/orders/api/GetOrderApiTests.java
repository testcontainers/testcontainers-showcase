package org.testcontainers.bookstore.orders.api;

import org.testcontainers.bookstore.common.AbstractIntegrationTest;
import org.testcontainers.bookstore.orders.domain.OrderService;
import org.testcontainers.bookstore.orders.domain.model.OrderConfirmationDTO;
import org.testcontainers.bookstore.orders.domain.model.OrderDTO;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class GetOrderApiTests extends AbstractIntegrationTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerName("Siva");
        createOrderRequest.setCustomerEmail("siva@gmail.com");
        createOrderRequest.setDeliveryAddressLine1("addr line 1");
        createOrderRequest.setDeliveryAddressLine2("addr line 2");
        createOrderRequest.setDeliveryAddressCity("Hyderabad");
        createOrderRequest.setDeliveryAddressState("Telangana");
        createOrderRequest.setDeliveryAddressZipCode("500072");
        createOrderRequest.setDeliveryAddressCountry("India");
        createOrderRequest.setCardNumber("1234123412341234");
        createOrderRequest.setCvv("123");
        createOrderRequest.setExpiryMonth(10);
        createOrderRequest.setExpiryYear(2025);
        createOrderRequest.setItems(Set.of(
                new CreateOrderRequest.LineItem("P100", "Product 1", BigDecimal.TEN, 1)
        ));
        OrderConfirmationDTO orderConfirmationDTO = orderService.createOrder(createOrderRequest);

        OrderDTO orderDTO = given()
                .when()
                .get("/api/orders/{orderId}", orderConfirmationDTO.getOrderId())
                .then()
                .statusCode(200)
                .extract().body().as(OrderDTO.class);

        assertThat(orderDTO.getOrderId()).isEqualTo(orderConfirmationDTO.getOrderId());
        assertThat(orderDTO.getItems()).hasSize(1);
    }

    @Test
    void shouldReturnNotFoundWhenOrderIdNotExist() {
        given()
                .when()
                .get("/api/orders/{orderId}", "non-existing-order-id")
                .then()
                .statusCode(404);
    }
}