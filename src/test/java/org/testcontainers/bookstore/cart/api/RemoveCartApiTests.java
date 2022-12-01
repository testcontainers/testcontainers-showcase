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
import static org.assertj.core.api.Assertions.assertThat;

public class RemoveCartApiTests extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @Autowired
    private CartRepository cartRepository;

    //@Test
    @RepeatedTest(1)
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

    @Test
    void shouldRemoveCart2() {
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