package org.testcontainers.bookstore.redpanda.orders.api;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.bookstore.orders.api.CreateOrderApiTests;
import org.testcontainers.redpanda.RedpandaContainer;

public class RedpandaCreateOrderApiTests extends CreateOrderApiTests {
    @BeforeAll
    static void setupKafka() {
        kafka = new RedpandaContainer("docker.redpanda.com/vectorized/redpanda:v22.2.1");
    }

}