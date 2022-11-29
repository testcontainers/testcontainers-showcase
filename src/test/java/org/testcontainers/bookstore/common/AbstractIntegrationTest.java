package org.testcontainers.bookstore.common;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.*;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    protected static Network network = Network.newNetwork();

    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
    protected static final MongoDBContainer mongodb = new MongoDBContainer("mongo:4.2");
    protected static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));
    protected static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.0.5-alpine")).withExposedPorts(6379);

    protected static final ToxiproxyContainer toxi = new ToxiproxyContainer("shopify/toxiproxy:2.1.0");

    @BeforeAll
    static void beforeAll() {
        //System.out.println("=================beforeAll=====================");
        Startables.deepStart(mongodb, postgres, redis, kafka, toxi).join();

    }

    @AfterAll
    static void afterAll() {
        //System.out.println("=================afterAll=====================");
        kafka.stop();
        redis.stop();
        postgres.stop();
        mongodb.stop();
        toxi.stop();
    }

    protected static void overridePropertiesInternal(DynamicPropertyRegistry registry) {
        //System.out.println("=================overridePropertiesInternal=====================");
        registry.add("spring.data.mongodb.uri", mongodb::getReplicaSetUrl);
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
}
