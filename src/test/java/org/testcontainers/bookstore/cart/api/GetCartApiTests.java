package org.testcontainers.bookstore.cart.api;

import org.testcontainers.bookstore.cart.domain.Cart;
import org.testcontainers.bookstore.cart.domain.CartRepository;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Set;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class GetCartApiTests extends AbstractIntegrationTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void shouldGetNewCart() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/carts")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("items", hasSize(0))
        ;
    }

    @Test
    void shouldGetNotFoundWhenCartIdNotExist() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/carts?cartId=non-existing-cart-id")
                .then()
                .statusCode(404)
        ;
    }

    @Test
    void shouldGetExistingCart() {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(new Cart(cartId, Set.of()));
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(0))
        ;
    }
}