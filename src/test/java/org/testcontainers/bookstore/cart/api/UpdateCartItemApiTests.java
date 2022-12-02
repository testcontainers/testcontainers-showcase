package org.testcontainers.bookstore.cart.api;

import io.restassured.http.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.bookstore.cart.domain.Cart;
import org.testcontainers.bookstore.cart.domain.CartItem;
import org.testcontainers.bookstore.cart.domain.CartRepository;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;

import java.math.BigDecimal;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class UpdateCartItemApiTests extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }

    @Autowired
    private CartRepository cartRepository;


    @RepeatedTest(4)
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
                .body("items[0].quantity", is(4));
    }


    @RepeatedTest(4)
    void notFoundOnNonExistingCartUpdate() {
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
                .put("/api/carts?cartId={cartId}", "non-existent")
                .then()
                .statusCode(404);
    }


    @RepeatedTest(4)
    void notFoundOnNonExistingProductsInCartQuantity() {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(new Cart(cartId, Set.of(
                new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 2)
        )));
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "productCode": "non-existent",
                                    "quantity": 4
                                }
                                """
                )
                .when()
                .put("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(404);
    }


    @RepeatedTest(4)
    void updatesOnlyOneProductInCartQuantity() {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(new Cart(cartId, Set.of(
                new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 2),
                new CartItem("P200", "Product 2", "P200 desc", BigDecimal.ONE, 5)
        )));

        Cart returnedCart = given()
                .contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "productCode": "P100",
                                    "quantity": 1
                                }
                                """
                )
                .when()
                .put("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(2))
                .extract().as(Cart.class);

        List<CartItem> returnedItems = new ArrayList<>(returnedCart.getItems());
        returnedItems.sort(Comparator.comparingInt(CartItem::getQuantity));

        Assertions.assertThat(returnedItems.get(0).getProductCode()).isEqualTo("P100");
        Assertions.assertThat(returnedItems.get(0).getQuantity()).isEqualTo(1);
        Assertions.assertThat(returnedItems.get(1).getProductCode()).isEqualTo("P200");
        Assertions.assertThat(returnedItems.get(1).getQuantity()).isEqualTo(5);
    }


    @RepeatedTest(4)
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