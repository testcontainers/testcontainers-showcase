package org.testcontainers.bookstore.cart.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.bookstore.cart.domain.Cart;
import org.testcontainers.bookstore.cart.domain.CartRepository;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;

public class GetCartApiTests extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @Autowired
    private CartRepository cartRepository;

    @Test
    void shouldGetNewCart() {
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/carts")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("items", hasSize(0));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "non-existing-cart-id-1",
                "non-existing-cart-id-2",
                "non-existing-cart-id-3",
                "non-existing-cart-id-4"
            })
    void shouldGetNotFoundWhenCartIdNotExist(String cartId) {
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(404);
    }

    @ParameterizedTest
    @ValueSource(strings = {"cart-id-1", "cart-id-2", "cart-id-3", "cart-id-4"})
    void shouldGetExistingCart(String cartId) {
        cartRepository.save(new Cart(cartId, Set.of()));
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(0));
    }
}
