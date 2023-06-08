package org.testcontainers.bookstore.catalog.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.testcontainers.bookstore.catalog.clients.promotions.Promotion;
import org.testcontainers.bookstore.catalog.clients.promotions.PromotionServiceClient;

@Service
public class ProductService {
    private final PromotionServiceClient promotionServiceClient;
    private final ProductRepository productRepository;

    public ProductService(PromotionServiceClient promotionServiceClient, ProductRepository productRepository) {
        this.promotionServiceClient = promotionServiceClient;
        this.productRepository = productRepository;
    }

    public PagedResult<Product> getProducts(int pageNo, int pageSize) {
        int page = pageNo <= 1 ? 0 : pageNo - 1;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.ASC, "name");
        Page<Product> productsPage = productRepository.findAll(pageable);
        var productsWithDiscount = applyPromotionDiscount(productsPage);
        return new PagedResult<>(productsWithDiscount);
    }

    public Optional<Product> getProductByCode(String code) {
        return productRepository.findByCode(code).map(product -> {
            Optional<Promotion> promotion = promotionServiceClient.getPromotion(product.getCode());
            promotion.ifPresent(value -> product.setPrice(product.getPrice().subtract(value.getDiscount())));
            return product;
        });
    }

    private Page<Product> applyPromotionDiscount(Page<Product> productsPage) {
        List<String> productCodes =
                productsPage.getContent().stream().map(Product::getCode).toList();
        List<Promotion> promotions = promotionServiceClient.getPromotions(productCodes);
        Map<String, BigDecimal> promotionsMap =
                promotions.stream().collect(Collectors.toMap(Promotion::getProductCode, Promotion::getDiscount));
        return productsPage.map(product -> new Product(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl(),
                promotionsMap.containsKey(product.getCode())
                        ? product.getPrice().subtract(promotionsMap.get(product.getCode()))
                        : product.getPrice()));
    }
}
