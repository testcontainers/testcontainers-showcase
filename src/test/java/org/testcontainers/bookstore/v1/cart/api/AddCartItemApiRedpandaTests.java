package org.testcontainers.bookstore.v1.cart.api;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.redpanda.RedpandaContainer;

class AddCartItemApiRedpandaTests extends AddCartItemApiTests {
  @BeforeAll
  static void setupKafka() {
    kafka = new RedpandaContainer("docker.redpanda.com/vectorized/redpanda:v22.2.1");
  }
}