package org.testcontainers.bookstore.redpanda.orders.event.handlers;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.bookstore.orders.event.handlers.OrderCreatedEventHandlerTest;
import org.testcontainers.redpanda.RedpandaContainer;

public class RedpandaOrderCreatedEventHandlerTest extends OrderCreatedEventHandlerTest {
    @BeforeAll
    static void setupKafka() {
        kafka = new RedpandaContainer("docker.redpanda.com/vectorized/redpanda:v22.2.1");
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
    }
}
