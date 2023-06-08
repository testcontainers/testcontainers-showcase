package org.testcontainers.bookstore.orders.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;
import org.testcontainers.bookstore.orders.domain.OrderService;
import org.testcontainers.bookstore.orders.domain.model.OrderConfirmationDTO;
import org.testcontainers.bookstore.orders.domain.model.OrderDTO;

public class GetOrderApiTests extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @Autowired
    private OrderService orderService;

    @ParameterizedTest
    @CsvSource({"P100", "P101", "P102", "P103"})
    void shouldCreateOrderSuccessfully(String productCode) {
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
        createOrderRequest.setItems(
                Set.of(new CreateOrderRequest.LineItem(productCode, "Product name", BigDecimal.TEN, 1)));
        OrderConfirmationDTO orderConfirmationDTO = orderService.createOrder(createOrderRequest);

        OrderDTO orderDTO = given().when()
                .get("/api/orders/{orderId}", orderConfirmationDTO.getOrderId())
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(OrderDTO.class);

        assertThat(orderDTO.getOrderId()).isEqualTo(orderConfirmationDTO.getOrderId());
        assertThat(orderDTO.getItems()).hasSize(1);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"non-existing-order-1", "non-existing-order-2", "non-existing-order-3", "non-existing-order-4"})
    void shouldReturnNotFoundWhenOrderIdNotExist(String orderId) {
        given().when().get("/api/orders/{orderId}", orderId).then().statusCode(404);
    }
}
