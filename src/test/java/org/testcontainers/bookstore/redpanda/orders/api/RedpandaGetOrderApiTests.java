package org.testcontainers.bookstore.redpanda.orders.api;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.bookstore.orders.api.GetOrderApiTests;
import org.testcontainers.redpanda.RedpandaContainer;

public class RedpandaGetOrderApiTests extends GetOrderApiTests {
    @BeforeAll
    static void setupKafka() {
        kafka = new RedpandaContainer("docker.redpanda.com/vectorized/redpanda:v22.2.1");
    }

}