package org.testcontainers.bookstore.common;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

import io.restassured.RestAssured;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.bookstore.catalog.domain.Product;
import org.testcontainers.bookstore.catalog.domain.ProductRepository;
import org.testcontainers.bookstore.notifications.NotificationService;
import org.testcontainers.bookstore.payment.domain.CreditCard;
import org.testcontainers.bookstore.payment.domain.CreditCardRepository;
import org.testcontainers.containers.*;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.redpanda.RedpandaContainer;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(TimeChecker.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
    protected static final MongoDBContainer mongodb = new MongoDBContainer("mongo:4.2");
    protected static GenericContainer<?> kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));
    protected static final GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:7.0.5-alpine")).withExposedPorts(6379);
    protected static final MockServerContainer mockServer =
            new MockServerContainer(DockerImageName.parse("jamesdbloom/mockserver:mockserver-5.13.2"));

    protected static MockServerClient mockServerClient;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @MockBean
    protected NotificationService notificationService;

    @LocalServerPort
    private Integer port;

    protected List<Product> products = List.of(
            new Product(null, "P100", "Product 1", "Product 1 desc", null, BigDecimal.TEN),
            new Product(null, "P101", "Product 2", "Product 2 desc", null, BigDecimal.valueOf(24)),
            new Product(null, "P102", "Product 3", "Product 3 desc", null, BigDecimal.valueOf(34)),
            new Product(null, "P103", "Product 4", "Product 4 desc", null, BigDecimal.valueOf(44)));
    protected List<CreditCard> creditCards = List.of(
            new CreditCard(null, "John", "1111222233334444", "123", 2, 2030),
            new CreditCard(null, "Siva", "1234123412341234", "123", 10, 2030),
            new CreditCard(null, "Kevin", "1234567890123456", "123", 3, 2030));

    @BeforeEach
    void setUpBase() {
        RestAssured.baseURI = "http://localhost:" + port;
        productRepository.deleteAll();
        productRepository.saveAll(products);

        creditCardRepository.deleteAllInBatch();
        creditCardRepository.saveAll(creditCards);
    }

    @AfterAll
    static void afterAllBase() {
        mockServer.stop();
        kafka.stop();
        redis.stop();
        postgres.stop();
        mongodb.stop();
    }

    protected static void overridePropertiesInternal(DynamicPropertyRegistry registry) {
        Startables.deepStart(mongodb, postgres, redis, kafka, mockServer).join();

        registry.add("spring.data.mongodb.uri", mongodb::getReplicaSetUrl);
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("spring.kafka.bootstrap-servers", getBootstrapUrl());
        registry.add("app.promotion-service-url", mockServer::getEndpoint);
        mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    }

    @NotNull private static Supplier<Object> getBootstrapUrl() {
        if (kafka instanceof KafkaContainer kafkaContainer) return kafkaContainer::getBootstrapServers;
        if (kafka instanceof RedpandaContainer redpanda) return redpanda::getBootstrapServers;
        else throw new RuntimeException("Unknown Kafka");
    }

    protected static void mockGetPromotions() {
        mockServerClient
                .when(request().withMethod("GET").withPath("/api/promotions?productCodes=.*"))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(new Header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(
                                        json(
                                                """
                                                [
                                                    {
                                                        "productCode": "P100",
                                                        "discount": 2.5
                                                    },
                                                    {
                                                        "productCode": "P101",
                                                        "discount": 1.5
                                                    }
                                                ]
                                                """)));
    }

    protected static void mockGetPromotion(String productCode, BigDecimal discount) {
        mockServerClient
                .when(request().withMethod("GET").withPath("/api/promotions/.*"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeaders(new Header("Content-Type", "application/json; charset=utf-8"))
                        .withBody(json(
                                """
                                                    {
                                                        "productCode": "%s",
                                                        "discount": %f
                                                    }
                                                """
                                        .formatted(productCode, discount.doubleValue()))));
    }
}
