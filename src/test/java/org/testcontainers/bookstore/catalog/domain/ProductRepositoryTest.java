package org.testcontainers.bookstore.catalog.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataMongoTest
@Testcontainers
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Container
    static final MongoDBContainer mongodb = new MongoDBContainer("mongo:4.2");

    @DynamicPropertySource
    static void overridePropertiesInternal(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongodb::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        productRepository.save(new Product(null, "P100", "Product 1", "Product 1 desc", null, BigDecimal.TEN));
        productRepository.save(new Product(null, "P101", "Product 2", "Product 2 desc", null, BigDecimal.valueOf(24)));
        productRepository.save(new Product(null, "P102", "Product 3", "Product 3 desc", null, BigDecimal.valueOf(34)));
        productRepository.save(new Product(null, "P103", "Product 4", "Product 4 desc", null, BigDecimal.valueOf(44)));
    }

    @Test
    void shouldGetAllProducts() {
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(4);
    }

    @ParameterizedTest
    @CsvSource({"P100, 10", "P101, 24", "P102, 34", "P103, 44"})
    void shouldFailToProductWithDuplicateCode(String productCode, BigDecimal price) {
        var product = new Product(null, productCode, "Product name", "Product desc", null, price);
        Assertions.assertThrows(DuplicateKeyException.class, () -> productRepository.save(product));
    }

    @ParameterizedTest
    @CsvSource({"P100, 10", "P101, 24", "P102, 34", "P103, 44"})
    void shouldGetProductByCode(String productCode, BigDecimal price) {
        Optional<Product> optionalProduct = productRepository.findByCode(productCode);
        assertThat(optionalProduct).isNotEmpty();
        assertThat(optionalProduct.get().getCode()).isEqualTo(productCode);
        assertThat(optionalProduct.get().getPrice()).isEqualTo(price);
    }
}
