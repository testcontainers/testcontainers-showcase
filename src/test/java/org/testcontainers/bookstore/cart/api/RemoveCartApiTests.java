package org.testcontainers.bookstore.cart.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.bookstore.cart.domain.Cart;
import org.testcontainers.bookstore.cart.domain.CartItem;
import org.testcontainers.bookstore.cart.domain.CartRepository;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;

public class RemoveCartApiTests extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @Autowired
    private CartRepository cartRepository;

    @ParameterizedTest
    @ValueSource(strings = {"cart-id-1", "cart-id-2", "cart-id-3", "cart-id-4"})
    void shouldRemoveCart(String cartId) {
        cartRepository.save(
                new Cart(cartId, Set.of(new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 2))));
        given().when().delete("/api/carts?cartId={cartId}", cartId).then().statusCode(200);
        assertThat(cartRepository.findById(cartId)).isEmpty();
    }
}
