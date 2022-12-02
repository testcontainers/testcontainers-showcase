package org.testcontainers.bookstore.payment.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class PaymentControllerTest extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @RepeatedTest(4)
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


    @RepeatedTest(4)
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


    @RepeatedTest(4)
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