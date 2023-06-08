package org.testcontainers.bookstore.redpanda.cart.api;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.bookstore.cart.api.RemoveCartApiTests;
import org.testcontainers.redpanda.RedpandaContainer;

public class RedpandaRemoveCartApiTests extends RemoveCartApiTests {

    @BeforeAll
    static void setupKafka() {
        kafka = new RedpandaContainer("docker.redpanda.com/vectorized/redpanda:v22.2.1");
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }
}
