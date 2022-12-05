package org.testcontainers.bookstore.redpanda.cart.api;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.bookstore.cart.api.AddCartItemApiTests;
import org.testcontainers.redpanda.RedpandaContainer;

class RedpandaAddCartItemApiTests extends AddCartItemApiTests {
    @BeforeAll
    static void setupKafka() {
        kafka = new RedpandaContainer("docker.redpanda.com/vectorized/redpanda:v22.2.1");
    }

}