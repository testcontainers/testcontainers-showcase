package org.testcontainers.bookstore.catalog.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String productCode) {
        super("Product with code: " + productCode + " not found");
    }
}
