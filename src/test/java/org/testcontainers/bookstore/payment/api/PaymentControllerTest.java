package org.testcontainers.bookstore.payment.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class PaymentControllerTest extends AbstractIntegrationTest {

    @ParameterizedTest
    @CsvSource({
            "1111222233334444, 123, 2, 2030",
            "1234123412341234, 123, 10, 2030",
            "1234567890123456, 123, 3, 2030"
    })
    void shouldAuthorizePaymentSuccessfully(String cardNumber, String cvv, int expiryMonth, int expiryYear) {
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "cardNumber": "%s",
                                    "cvv": "%s",
                                    "expiryMonth": "%d",
                                    "expiryYear": "%d"
                                }
                                """.formatted(cardNumber, cvv, expiryMonth, expiryYear)
                )
                .when()
                .post("/api/payments/authorize")
                .then()
                .statusCode(200)
                .body("status", is("ACCEPTED"));
    }


    @ParameterizedTest
    @CsvSource({
            "1111222233334444, 111, 2, 2030",
            "1234123412341234, 222, 10, 2030",
            "1234567890123456, 333, 3, 2030"
    })
    void shouldRejectPaymentWhenCVVIsIncorrect(String cardNumber, String cvv, int expiryMonth, int expiryYear) {
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "cardNumber": "%s",
                                    "cvv": "%s",
                                    "expiryMonth": "%d",
                                    "expiryYear": "%d"
                                }
                                """.formatted(cardNumber, cvv, expiryMonth, expiryYear)
                )
                .when()
                .post("/api/payments/authorize")
                .then()
                .statusCode(200)
                .body("status", is("REJECTED"));
    }


    @ParameterizedTest
    @CsvSource({
            "1111222233334444, 111",
            "1234123412341234, 222",
            "1234567890123456, 333"
    })
    void shouldFailWhenMandatoryDataIsMissing(String cardNumber, String cvv) {
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                                {
                                   "cardNumber": "%s",
                                    "cvv": "%s"
                                }
                                """.formatted(cardNumber, cvv)
                )
                .when()
                .post("/api/payments/authorize")
                .then()
                .statusCode(400);
    }
}