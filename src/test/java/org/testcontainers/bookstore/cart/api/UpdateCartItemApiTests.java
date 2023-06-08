package org.testcontainers.bookstore.cart.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.bookstore.cart.domain.Cart;
import org.testcontainers.bookstore.cart.domain.CartItem;
import org.testcontainers.bookstore.cart.domain.CartRepository;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;

public class UpdateCartItemApiTests extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @Autowired
    private CartRepository cartRepository;

    @ParameterizedTest
    @ValueSource(strings = {"P100", "P101", "P102", "P103"})
    void shouldUpdateItemQuantity(String productCode) {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(
                new Cart(cartId, Set.of(new CartItem(productCode, "Product name", "Product desc", BigDecimal.TEN, 2))));
        given().contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "productCode": "%s",
                                    "quantity": 4
                                }
                                """
                                .formatted(productCode))
                .when()
                .put("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(1))
                .body("items[0].productCode", is(productCode))
                .body("items[0].quantity", is(4));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "non-existing-cart-id-1",
                "non-existing-cart-id-2",
                "non-existing-cart-id-3",
                "non-existing-cart-id-4"
            })
    void notFoundOnNonExistingCartUpdate(String cartId) {
        cartRepository.save(new Cart(
                UUID.randomUUID().toString(),
                Set.of(new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 2))));
        given().contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "productCode": "P100",
                                    "quantity": 4
                                }
                                """)
                .when()
                .put("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(404);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "non-existing-product-code-1",
                "non-existing-product-code-2",
                "non-existing-product-code-3",
                "non-existing-product-code-4"
            })
    void notFoundOnNonExistingProductsInCartQuantity(String productCode) {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(
                new Cart(cartId, Set.of(new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 2))));
        given().contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "productCode": "%s",
                                    "quantity": 4
                                }
                                """
                                .formatted(productCode))
                .when()
                .put("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(404);
    }

    @ParameterizedTest
    @ValueSource(strings = {"cart-id-1", "cart-id-2", "cart-id-3", "cart-id-4"})
    void updatesOnlyOneProductInCartQuantity(String cartId) {
        cartRepository.save(new Cart(
                cartId,
                Set.of(
                        new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 2),
                        new CartItem("P101", "Product 2", "P101 desc", BigDecimal.ONE, 5))));

        Cart returnedCart = given().contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "productCode": "P100",
                                    "quantity": 1
                                }
                                """)
                .when()
                .put("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(2))
                .extract()
                .as(Cart.class);

        List<CartItem> returnedItems = new ArrayList<>(returnedCart.getItems());
        returnedItems.sort(Comparator.comparingInt(CartItem::getQuantity));

        Assertions.assertThat(returnedItems.get(0).getProductCode()).isEqualTo("P100");
        Assertions.assertThat(returnedItems.get(0).getQuantity()).isEqualTo(1);
        Assertions.assertThat(returnedItems.get(1).getProductCode()).isEqualTo("P101");
        Assertions.assertThat(returnedItems.get(1).getQuantity()).isEqualTo(5);
    }

    @ParameterizedTest
    @ValueSource(strings = {"P100", "P101", "P102", "P103"})
    void shouldRemoveItemWhenUpdatedItemQuantityIsZero(String productCode) {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(
                new Cart(cartId, Set.of(new CartItem(productCode, "Product 1", "Product desc", BigDecimal.TEN, 2))));
        given().contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "productCode": "%s",
                                    "quantity": 0
                                }
                                """
                                .formatted(productCode))
                .when()
                .put("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(0));
    }
}
