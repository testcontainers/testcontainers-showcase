package org.testcontainers.bookstore.payment.api;

import org.testcontainers.bookstore.common.AbstractIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

class PaymentControllerTest extends AbstractIntegrationTest {

    @LocalServerPort
    private Integer port;


    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void shouldAuthorizePaymentSuccessfully() {
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                        {
                            "cardNumber": "1111222233334444",
                            "cvv": "123",
                            "expiryMonth": 2,
                            "expiryYear": 2030
                        }
                        """
                )
                .when()
                .post("/api/payments/authorize")
                .then()
                .statusCode(200)
                .body("status", is("ACCEPTED"));
    }

    @Test
    void shouldRejectPaymentWhenCVVIsIncorrect() {
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                        {
                            "cardNumber": "1111222233334444",
                            "cvv": "111",
                            "expiryMonth": 2,
                            "expiryYear": 2030
                        }
                        """
                )
                .when()
                .post("/api/payments/authorize")
                .then()
                .statusCode(200)
                .body("status", is("REJECTED"));
    }

    @Test
    void shouldFailWhenMandatoryDataIsMissing() {
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                        {
                            "cardNumber": "1111222233334444",
                            "cvv": "111"
                        }
                        """
                )
                .when()
                .post("/api/payments/authorize")
                .then()
                .statusCode(400);
    }
}