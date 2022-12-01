package org.testcontainers.bookstore.cart.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.bookstore.cart.domain.Cart;
import org.testcontainers.bookstore.cart.domain.CartRepository;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;

import java.util.Set;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GetCartApiTests extends AbstractIntegrationTest {
    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @Autowired
    private CartRepository cartRepository;

    //@Test
    @RepeatedTest(4)
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
    @RepeatedTest(4)
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
    @RepeatedTest(4)
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