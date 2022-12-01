package org.testcontainers.bookstore.cart.api;

import org.junit.jupiter.api.RepeatedTest;
import org.testcontainers.bookstore.cart.domain.Cart;
import org.testcontainers.bookstore.cart.domain.CartRepository;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Set;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class GetCartApiTests extends AbstractIntegrationTest {
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

    //@Test
    @RepeatedTest(5)
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

    //@Test
    @RepeatedTest(5)
    void shouldGetNotFoundWhenCartIdNotExist() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/carts?cartId=non-existing-cart-id")
                .then()
                .statusCode(404)
        ;
    }

    //@Test
    @RepeatedTest(5)
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

    @Test
    void shouldGetNewCart2() {
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
    void shouldGetNotFoundWhenCartIdNotExist2() {
        given()
          .contentType(ContentType.JSON)
          .when()
          .get("/api/carts?cartId=non-existing-cart-id")
          .then()
          .statusCode(404)
        ;
    }

    @Test
    void shouldGetExistingCart2() {
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