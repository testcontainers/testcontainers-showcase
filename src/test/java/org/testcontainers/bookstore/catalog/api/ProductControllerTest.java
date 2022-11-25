package org.testcontainers.bookstore.catalog.api;

import org.testcontainers.bookstore.catalog.domain.Product;
import org.testcontainers.bookstore.catalog.domain.ProductRepository;
import org.testcontainers.bookstore.common.AbstractIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

@TestPropertySource(properties = {
    "app.promotion-service-type=remote"
})
class ProductControllerTest extends AbstractIntegrationTest {

    static final MockServerContainer mockServer =
            new MockServerContainer(DockerImageName.parse("jamesdbloom/mockserver:mockserver-5.13.2"));

    protected static MockServerClient mockServerClient;

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
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overridePropertiesInternal(registry);
        registry.add("app.promotion-service-url", mockServer::getEndpoint);
        mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    }

    @LocalServerPort
    private Integer port;

    @Autowired
    private ProductRepository productRepository;

    private List<Product> products = List.of(
            new Product(null, "P100", "Product 1", "Product 1 desc", null, BigDecimal.TEN),
            new Product(null, "P101", "Product 2", "Product 2 desc", null, BigDecimal.valueOf(24))
    );

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        productRepository.deleteAll();
        productRepository.saveAll(products);
    }

    @Test
    void shouldGetAllProducts() {
        mockGetPromotions();

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/products")
                .then()
                .statusCode(200)
                .body("data", hasSize(products.size()))
                .body("totalElements", is(products.size()))
                .body("pageNumber", is(1))
                .body("totalPages", is(1))
                .body("isFirst", is(true))
                .body("isLast", is(true))
                .body("hasNext", is(false))
                .body("hasPrevious", is(false));
    }

    @Test
    void shouldGetProductByCode() {
        mockGetPromotion("P100", new BigDecimal("2.5"));
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/products/{code}", "P100")
                .then()
                .statusCode(200)
                .body("code", is("P100"))
                .body("name", is("Product 1"))
                .body("description", is("Product 1 desc"))
                .body("price", is(7.5f))
        ;
    }

    @Test
    void shouldReturnNotFoundWhenProductCodeNotExists() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/products/{code}", "invalid_product_code")
                .then()
                .statusCode(404);
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