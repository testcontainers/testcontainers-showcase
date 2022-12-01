package org.testcontainers.bookstore.orders.api;

import org.junit.jupiter.api.RepeatedTest;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;
import org.testcontainers.bookstore.orders.domain.OrderService;
import org.testcontainers.bookstore.orders.domain.entity.Order;
import org.testcontainers.bookstore.orders.domain.entity.OrderStatus;
import org.testcontainers.bookstore.orders.domain.model.OrderConfirmationDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

class CreateOrderApiTests extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @LocalServerPort
    private Integer port;

    @Autowired
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    //@Test
    @RepeatedTest(10)
    void shouldCreateOrderSuccessfully() {
        OrderConfirmationDTO orderConfirmationDTO = given()
                .contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "customerName": "Siva",
                                    "customerEmail": "siva@gmail.com",
                                    "deliveryAddressLine1": "Birkelweg",
                                    "deliveryAddressLine2": "Hans-Edenhofer-Straße 23",
                                    "deliveryAddressCity": "Berlin",
                                    "deliveryAddressState": "Berlin",
                                    "deliveryAddressZipCode": "94258",
                                    "deliveryAddressCountry": "Germany",
                                    "cardNumber": "1111222233334444",
                                    "cvv": "123",
                                    "expiryMonth": 2,
                                    "expiryYear": 2030,
                                    "items": [
                                        {
                                            "productCode": "P100",
                                            "productName": "Product 1",
                                            "productPrice": 25.50,
                                            "quantity": 1
                                        }
                                    ]
                                }
                                """
                )
                .when()
                .post("/api/orders")
                .then()
                .statusCode(202)
                .body("orderId", notNullValue())
                .body("orderStatus", is("NEW"))
                .extract().body().as(OrderConfirmationDTO.class);

        await().pollInterval(Duration.ofSeconds(5)).atMost(20, SECONDS).until(() -> {
            Optional<Order> orderOptional = orderService.findOrderByOrderId(orderConfirmationDTO.getOrderId());
            return orderOptional.isPresent() && orderOptional.get().getStatus() == OrderStatus.DELIVERED;
        });
    }

    //@Test
    @RepeatedTest(10)
    void shouldCreateOrderWithErrorStatusWhenPaymentRejected() {
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                        {
                            "customerName": "Siva",
                            "customerEmail": "siva@gmail.com",
                            "deliveryAddressLine1": "Birkelweg",
                            "deliveryAddressLine2": "Hans-Edenhofer-Straße 23",
                            "deliveryAddressCity": "Berlin",
                            "deliveryAddressState": "Berlin",
                            "deliveryAddressZipCode": "94258",
                            "deliveryAddressCountry": "Germany",
                            "cardNumber": "1111222233334444",
                            "cvv": "345",
                            "expiryMonth": 2,
                            "expiryYear": 2024,
                            "items": [
                                {
                                    "productCode": "P100",
                                    "productName": "Product 1",
                                    "productPrice": 25.50,
                                    "quantity": 1
                                }
                            ]
                        }
                        """
                )
                .when()
                .post("/api/orders")
                .then()
                .statusCode(202)
                .body("orderId", notNullValue())
                .body("orderStatus", is("ERROR"))
        ;
    }

    //@Test
    @RepeatedTest(10)
    void shouldReturnBadRequestWhenMandatoryDataIsMissing() {
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                        {
                            "cardNumber": "1111222233334444",
                            "cvv": "345",
                            "expiryMonth": 2,
                            "expiryYear": 2024,
                            "items": [
                                {
                                    "productCode": "P100",
                                    "productName": "Product 1",
                                    "productPrice": 25.50,
                                    "quantity": 1
                                }
                            ]
                        }
                        """
                )
                .when()
                .post("/api/orders")
                .then()
                .statusCode(400)
        ;
    }

    //@Test
    @RepeatedTest(10)
    void shouldCancelOrderWhenCanNotBeDelivered() {
        OrderConfirmationDTO orderConfirmationDTO = given()
                .contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "customerName": "Siva",
                                    "customerEmail": "siva@gmail.com",
                                    "deliveryAddressLine1": "Birkelweg",
                                    "deliveryAddressLine2": "Hans-Edenhofer-Straße 23",
                                    "deliveryAddressCity": "Turkey",
                                    "deliveryAddressState": "Turkey",
                                    "deliveryAddressZipCode": "94258",
                                    "deliveryAddressCountry": "Turkey",
                                    "cardNumber": "1111222233334444",
                                    "cvv": "123",
                                    "expiryMonth": 2,
                                    "expiryYear": 2030,
                                    "items": [
                                        {
                                            "productCode": "P100",
                                            "productName": "Product 1",
                                            "productPrice": 25.50,
                                            "quantity": 1
                                        }
                                    ]
                                }
                                """
                )
                .when()
                .post("/api/orders")
                .then()
                .statusCode(202)
                .body("orderId", notNullValue())
                .body("orderStatus", is("NEW"))
                .extract().body().as(OrderConfirmationDTO.class);

        await().atMost(15, SECONDS).until(() -> {
            Optional<Order> orderOptional = orderService.findOrderByOrderId(orderConfirmationDTO.getOrderId());
            return orderOptional.isPresent() && orderOptional.get().getStatus() == OrderStatus.CANCELLED;
        });
    }
}