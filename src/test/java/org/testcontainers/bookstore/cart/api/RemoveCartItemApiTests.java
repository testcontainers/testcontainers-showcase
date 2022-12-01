package org.testcontainers.bookstore.cart.api;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.bookstore.cart.domain.Cart;
import org.testcontainers.bookstore.cart.domain.CartItem;
import org.testcontainers.bookstore.cart.domain.CartRepository;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class RemoveCartItemApiTests extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @Autowired
    private CartRepository cartRepository;

    //@Test
    @RepeatedTest(1)
    void shouldRemoveItemFromCart() {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(new Cart(cartId, Set.of(
                new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 2)
        )));
        given()
                .when()
                .delete("/api/carts/items/{code}?cartId={cartId}", "P100", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(0))
        ;
    }

    //@Test
    @RepeatedTest(1)
    void shouldIgnoreDeletingNonExistentProductRemoveItemFromCart() {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(new Cart(cartId, Set.of(
          new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 2)
        )));
        given()
          .when()
          .delete("/api/carts/items/{code}?cartId={cartId}", "non-existing-productCode", cartId)
          .then()
          .statusCode(200)
          .body("id", is(cartId))
          .body("items", hasSize(1));
    }


    @Test
    void shouldRemoveItemFromCart2() {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(new Cart(cartId, Set.of(
          new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 2)
        )));
        given()
          .when()
          .delete("/api/carts/items/{code}?cartId={cartId}", "P100", cartId)
          .then()
          .statusCode(200)
          .body("id", is(cartId))
          .body("items", hasSize(0))
        ;
    }

    @Test
    void shouldIgnoreDeletingNonExistentProductRemoveItemFromCart2() {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(new Cart(cartId, Set.of(
          new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 2)
        )));
        given()
          .when()
          .delete("/api/carts/items/{code}?cartId={cartId}", "not-existing", cartId)
          .then()
          .statusCode(200)
          .body("id", is(cartId))
          .body("items", hasSize(1));
    }
}