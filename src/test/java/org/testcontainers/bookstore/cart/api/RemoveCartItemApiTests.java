package org.testcontainers.bookstore.cart.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.bookstore.cart.domain.Cart;
import org.testcontainers.bookstore.cart.domain.CartItem;
import org.testcontainers.bookstore.cart.domain.CartRepository;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;

public class RemoveCartItemApiTests extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @Autowired
    private CartRepository cartRepository;

    @ParameterizedTest
    @ValueSource(strings = {"P100", "P101", "P102", "P103"})
    void shouldRemoveItemFromCart(String productCode) {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(
                new Cart(cartId, Set.of(new CartItem(productCode, "Product name", "Product desc", BigDecimal.TEN, 2))));
        given().when()
                .delete("/api/carts/items/{code}?cartId={cartId}", productCode, cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(0));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "non-existing-productCode-1",
                "non-existing-productCode-2",
                "non-existing-productCode-3",
                "non-existing-productCode-4"
            })
    void shouldIgnoreDeletingNonExistentProductRemoveItemFromCart(String productCode) {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(
                new Cart(cartId, Set.of(new CartItem("P100", "Product name", "Product desc", BigDecimal.TEN, 2))));
        given().when()
                .delete("/api/carts/items/{code}?cartId={cartId}", productCode, cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(1));
    }
}
