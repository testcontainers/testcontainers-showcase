package org.testcontainers.bookstore.redpanda.cart.api;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.bookstore.cart.api.RemoveCartApiTests;
import org.testcontainers.redpanda.RedpandaContainer;

public class RedpandaRemoveCartApiTests extends RemoveCartApiTests {

    @BeforeAll
    static void setupKafka() {
        kafka = new RedpandaContainer("docker.redpanda.com/vectorized/redpanda:v22.2.1");
    }

}