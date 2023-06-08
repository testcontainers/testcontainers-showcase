package org.testcontainers.bookstore.cart.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.bookstore.cart.domain.Cart;
import org.testcontainers.bookstore.cart.domain.CartItem;
import org.testcontainers.bookstore.cart.domain.CartRepository;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;

public class AddCartItemApiTests extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @Autowired
    private CartRepository cartRepository;

    @ParameterizedTest
    @ValueSource(strings = {"P100", "P101", "P102", "P103"})
    public void shouldAddItemToNewCart(String productCode) {
        given().contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "productCode": "%s",
                                    "quantity": 2
                                }
                                """
                                .formatted(productCode))
                .when()
                .post("/api/carts")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("items", hasSize(1));
    }

    @ParameterizedTest
    @CsvSource({"P100, 1", "P101, 2", "P102, 1", "P103, 3"})
    void shouldAddItemToExistingCart(String productCode, int quantity) {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(new Cart(cartId, Set.of()));
        given().contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "productCode": "%s",
                                    "quantity": %d
                                }
                                """
                                .formatted(productCode, quantity))
                .when()
                .post("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(1))
                .body("items[0].productCode", is(productCode))
                .body("items[0].quantity", is(quantity));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "non-existing-cart-id-1",
                "non-existing-cart-id-2",
                "non-existing-cart-id-3",
                "non-existing-cart-id-4"
            })
    void shouldGetNotFoundWhenAddItemToNonExistingCart(String cartId) {
        given().contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "productCode": "P100",
                                    "quantity": 1
                                }
                                """)
                .when()
                .post("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(404);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "non-existing-product-code-1",
                "non-existing-product-code-2",
                "non-existing-product-code-3",
                "non-existing-product-code-4"
            })
    void shouldGetNotFoundWhenAddInvalidItemToCart(String productCode) {
        given().contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "productCode": "%s",
                                    "quantity": 2
                                }
                                """
                                .formatted(productCode))
                .when()
                .post("/api/carts")
                .then()
                .statusCode(404);
    }

    @ParameterizedTest
    @ValueSource(strings = {"P100", "P101", "P102", "P103"})
    void shouldAddItemIncreaseQuantityWhenAddingSameProduct(String productCode) {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(
                new Cart(cartId, Set.of(new CartItem(productCode, "Product 1", "P100 desc", BigDecimal.TEN, 1))));
        given().contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "productCode": "%s",
                                    "quantity": 1
                                }
                                """
                                .formatted(productCode))
                .when()
                .post("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(1))
                .body("items[0].productCode", is(productCode))
                .body("items[0].quantity", is(2));
    }

    @ParameterizedTest
    @ValueSource(strings = {"P101", "P102", "P103"})
    void shouldAddDifferentProduct(String productCode) {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(
                new Cart(cartId, Set.of(new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 1))));
        given().contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "productCode": "%s",
                                    "quantity": 1
                                }
                                """
                                .formatted(productCode))
                .when()
                .post("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(2));
    }
}
