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
import static org.assertj.core.api.Assertions.assertThat;

class RemoveCartApiTests extends AbstractIntegrationTest {

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
    void shouldRemoveCart() {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(new Cart(cartId, Set.of(
                new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 2)
        )));
        given()
                .when()
                .delete("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200);
        assertThat(cartRepository.findById(cartId)).isEmpty();
    }
}