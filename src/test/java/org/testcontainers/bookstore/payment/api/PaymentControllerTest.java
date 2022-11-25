package org.testcontainers.bookstore.payment.api;

import org.testcontainers.bookstore.common.AbstractIntegrationTest;
import org.testcontainers.bookstore.payment.domain.CreditCard;
import org.testcontainers.bookstore.payment.domain.CreditCardRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

class PaymentControllerTest extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @LocalServerPort
    private Integer port;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        creditCardRepository.deleteAllInBatch();
        creditCardRepository.save( new CreditCard(null, "Siva", "1111222233334444", "123", 2, 2025));
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
                            "expiryYear": 2025
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
                            "expiryYear": 2024
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