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
import static org.hamcrest.Matchers.notNullValue;

class AddCartItemApiTests extends AbstractIntegrationTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void shouldAddItemToNewCart() {
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                        {
                            "productCode": "P100",
                            "quantity": 2
                        }
                        """
                )
                .when()
                .post("/api/carts")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("items", hasSize(1))
                ;
    }

    @Test
    void shouldAddItemToExistingCart() {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(new Cart(cartId, Set.of()));
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                        {
                            "productCode": "P100",
                            "quantity": 2
                        }
                        """
                )
                .when()
                .post("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(1))
                .body("items[0].productCode", is("P100"))
                .body("items[0].quantity", is(2))
        ;
    }

    @Test
    void shouldGetNotFoundWhenAddItemToNonExistingCart() {
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                        {
                            "productCode": "P100",
                            "quantity": 2
                        }
                        """
                )
                .when()
                .post("/api/carts?cartId={cartId}", "non-existing-cart-id")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldGetNotFoundWhenAddInvalidItemToCart() {
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                        {
                            "productCode": "non-existing-product-id",
                            "quantity": 2
                        }
                        """
                )
                .when()
                .post("/api/carts")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldAddItemIncreaseQuantityWhenAddingSameProduct() {
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
                            "quantity": 1
                        }
                        """
                )
                .when()
                .post("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(1))
                .body("items[0].productCode", is("P100"))
                .body("items[0].quantity", is(3))
        ;
    }

    @Test
    void shouldAddDifferentProduct() {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(new Cart(cartId, Set.of(
                new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 2)
        )));
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                        {
                            "productCode": "P101",
                            "quantity": 1
                        }
                        """
                )
                .when()
                .post("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(2))
        ;
    }
}