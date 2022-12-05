package org.testcontainers.bookstore.redpanda.cart.api;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.bookstore.cart.api.UpdateCartItemApiTests;
import org.testcontainers.redpanda.RedpandaContainer;

public class RedpandaUpdateCartItemApiTests extends UpdateCartItemApiTests {
    @BeforeAll
    static void setupKafka() {
        kafka = new RedpandaContainer("docker.redpanda.com/vectorized/redpanda:v22.2.1");
    }

}