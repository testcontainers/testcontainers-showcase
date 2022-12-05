package org.testcontainers.bookstore.redpanda.orders.event.handlers;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.bookstore.orders.event.handlers.OrderCancelledEventHandlerTest;
import org.testcontainers.redpanda.RedpandaContainer;

public class RedpandaOrderCancelledEventHandlerTest extends OrderCancelledEventHandlerTest {
    @BeforeAll
    static void setupKafka() {
        kafka = new RedpandaContainer("docker.redpanda.com/vectorized/redpanda:v22.2.1");
    }
}