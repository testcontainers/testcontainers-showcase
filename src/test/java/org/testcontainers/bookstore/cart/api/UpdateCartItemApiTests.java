package org.testcontainers.bookstore.cart.api;

import org.testcontainers.bookstore.cart.domain.Cart;
import org.testcontainers.bookstore.cart.domain.CartItem;
import org.testcontainers.bookstore.cart.domain.CartRepository;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

class UpdateCartItemApiTests extends AbstractIntegrationTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void shouldUpdateItemQuantity() {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(new Cart(cartId, Set.of(
                new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 2)
        )));
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                        {
                            "productCode": "P100",
                            "quantity": 4
                        }
                        """
                )
                .when()
                .put("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(1))
                .body("items[0].productCode", is("P100"))
                .body("items[0].quantity", is(4))
        ;
    }

    @Test
    void shouldRemoveItemWhenUpdatedItemQuantityIsZero() {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(new Cart(cartId, Set.of(
                new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 2)
        )));
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                        {
                            "productCode": "P100",
                            "quantity": 0
                        }
                        """
                )
                .when()
                .put("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(0))
        ;
    }
}