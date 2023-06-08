package org.testcontainers.bookstore.cart.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.testcontainers.bookstore.cart.domain.Cart;
import org.testcontainers.bookstore.cart.domain.CartItem;
import org.testcontainers.bookstore.cart.domain.CartNotFoundException;
import org.testcontainers.bookstore.cart.domain.CartRepository;
import org.testcontainers.bookstore.catalog.domain.Product;
import org.testcontainers.bookstore.catalog.domain.ProductNotFoundException;
import org.testcontainers.bookstore.catalog.domain.ProductService;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    private final ProductService productService;
    private final CartRepository cartRepository;

    public CartController(ProductService productService, CartRepository cartRepository) {
        this.productService = productService;
        this.cartRepository = cartRepository;
    }

    @GetMapping
    public ResponseEntity<Cart> getCart(@RequestParam(name = "cartId", required = false) String cartId) {
        if (!StringUtils.hasText(cartId)) {
            Cart cart = cartRepository.save(Cart.withNewId());
            return ResponseEntity.ok(cart);
        }
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new CartNotFoundException("Cart not found"));
        return ResponseEntity.ok(cart);
    }

    @PostMapping
    public Cart addToCart(
            @RequestParam(name = "cartId", required = false) String cartId,
            @RequestBody CartItemRequestDTO cartItemRequest) {
        Cart cart;
        if (!StringUtils.hasText(cartId)) {
            cart = Cart.withNewId();
        } else {
            cart = cartRepository.findById(cartId).orElseThrow(() -> new CartNotFoundException(cartId));
        }
        log.info("Add productCode: {} to cart", cartItemRequest.getProductCode());
        Product product = productService
                .getProductByCode(cartItemRequest.getProductCode())
                .orElseThrow(() -> new ProductNotFoundException(cartItemRequest.getProductCode()));
        CartItem cartItem = new CartItem(
                product.getCode(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                cartItemRequest.getQuantity() > 0 ? cartItemRequest.getQuantity() : 1);
        cart.addItem(cartItem);
        return cartRepository.save(cart);
    }

    @PutMapping
    public Cart updateCartItemQuantity(
            @RequestParam(name = "cartId") String cartId, @RequestBody CartItemRequestDTO cartItemRequest) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new CartNotFoundException(cartId));
        log.info(
                "Update quantity: {} for productCode:{} quantity in cart: {}",
                cartItemRequest.getQuantity(),
                cartItemRequest.getProductCode(),
                cartId);
        Product product = productService
                .getProductByCode(cartItemRequest.getProductCode())
                .orElseThrow(() -> new ProductNotFoundException(cartItemRequest.getProductCode()));
        if (cartItemRequest.getQuantity() <= 0) {
            cart.removeItem(product.getCode());
        } else {
            cart.updateItemQuantity(product.getCode(), cartItemRequest.getQuantity());
        }
        return cartRepository.save(cart);
    }

    @DeleteMapping(value = "/items/{productCode}")
    public Cart removeCartItem(
            @RequestParam(name = "cartId") String cartId, @PathVariable("productCode") String productCode) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new CartNotFoundException(cartId));
        log.info("Remove cart line item productCode: {}", productCode);
        cart.removeItem(productCode);
        return cartRepository.save(cart);
    }

    @DeleteMapping
    public void removeCart(@RequestParam(name = "cartId") String cartId) {
        cartRepository.deleteById(cartId);
    }
}
