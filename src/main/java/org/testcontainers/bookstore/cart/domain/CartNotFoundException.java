package org.testcontainers.bookstore.cart.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CartNotFoundException extends RuntimeException {

    public CartNotFoundException(String cartId) {
        super("Cart with id: " + cartId + " not found");
    }
}
