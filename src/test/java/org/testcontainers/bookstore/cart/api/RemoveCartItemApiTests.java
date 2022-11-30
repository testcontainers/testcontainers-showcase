package org.testcontainers.bookstore.cart.api;

import org.testcontainers.bookstore.cart.domain.Cart;
import org.testcontainers.bookstore.cart.domain.CartItem;
import org.testcontainers.bookstore.cart.domain.CartRepository;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

class RemoveCartItemApiTests extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @LocalServerPort
    private Integer port;

    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
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

    @Test
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