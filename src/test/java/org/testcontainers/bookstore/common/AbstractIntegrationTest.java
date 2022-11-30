package org.testcontainers.bookstore.common;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Header;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.bookstore.notifications.NotificationService;
import org.testcontainers.containers.*;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
    protected static final MongoDBContainer mongodb = new MongoDBContainer("mongo:4.2");
    protected static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));
    protected static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.0.5-alpine")).withExposedPorts(6379);
    protected static final MockServerContainer mockServer = new MockServerContainer(DockerImageName.parse("jamesdbloom/mockserver:mockserver-5.13.2"));

    protected static MockServerClient mockServerClient;

    @MockBean
    protected NotificationService notificationService;

    @BeforeAll
    static void beforeAll() {
        Startables.deepStart(mongodb, postgres, redis, kafka, mockServer).join();

    }

    @AfterAll
    static void afterAll() {
        mockServer.stop();
        kafka.stop();
        redis.stop();
        postgres.stop();
        mongodb.stop();
        //toxi.stop();
    }

    protected static void overridePropertiesInternal(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongodb::getReplicaSetUrl);
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("app.promotion-service-url", mockServer::getEndpoint);
        mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    }

    protected static void mockGetPromotions() {
        mockServerClient.when(
                        request().withMethod("GET").withPath("/api/promotions?productCodes=.*"))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(new Header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(json(
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
                                                """
                                ))
                );
    }

    protected static void mockGetPromotion(String productCode, BigDecimal discount) {
        mockServerClient.when(
                        request().withMethod("GET").withPath("/api/promotions/.*"))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(new Header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(json(
                                        """
                                            {
                                                "productCode": "%s",
                                                "discount": %f
                                            }
                                        """.formatted(productCode, discount.doubleValue())
                                ))
                );
    }
}
