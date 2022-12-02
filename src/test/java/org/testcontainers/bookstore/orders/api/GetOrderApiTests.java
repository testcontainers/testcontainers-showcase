package org.testcontainers.bookstore.orders.api;

import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;
import org.testcontainers.bookstore.orders.domain.OrderService;
import org.testcontainers.bookstore.orders.domain.model.OrderConfirmationDTO;
import org.testcontainers.bookstore.orders.domain.model.OrderDTO;

import java.math.BigDecimal;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class GetOrderApiTests extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @Autowired
    private OrderService orderService;

    @RepeatedTest(4)
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


    @RepeatedTest(4)
    void shouldReturnNotFoundWhenOrderIdNotExist() {
        given()
                .when()
                .get("/api/orders/{orderId}", "non-existing-order-id")
                .then()
                .statusCode(404);
    }
}