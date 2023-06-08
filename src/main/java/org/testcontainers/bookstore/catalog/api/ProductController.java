package org.testcontainers.bookstore.catalog.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.testcontainers.bookstore.catalog.domain.PagedResult;
import org.testcontainers.bookstore.catalog.domain.Product;
import org.testcontainers.bookstore.catalog.domain.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public PagedResult<Product> getProducts(
            @RequestParam(name = "page", defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "20") int pageSize) {
        return productService.getProducts(pageNo, pageSize);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Product> getProductByCode(@PathVariable String code) {
        return productService.getProductByCode(code).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound()
                .build());
    }
}
