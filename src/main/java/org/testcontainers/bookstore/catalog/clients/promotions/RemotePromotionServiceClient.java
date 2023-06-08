package org.testcontainers.bookstore.catalog.clients.promotions;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.bookstore.ApplicationProperties;

@Service
@ConditionalOnProperty(name = "app.promotion-service-type", havingValue = "remote", matchIfMissing = true)
public class RemotePromotionServiceClient implements PromotionServiceClient {
    private static final Logger log = LoggerFactory.getLogger(RemotePromotionServiceClient.class);

    private final ApplicationProperties properties;

    public RemotePromotionServiceClient(ApplicationProperties properties) {
        this.properties = properties;
    }

    @Override
    public List<Promotion> getPromotions(List<String> productCodes) {
        log.info("Fetching promotions from remote service for productCodes: {}", productCodes);
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> httpEntity = new HttpEntity<>(headers);
            String productCodesCsv = String.join(",", productCodes);
            ResponseEntity<List<Promotion>> response = restTemplate.exchange(
                    properties.promotionServiceUrl() + "/api/promotions?productCodes=" + productCodesCsv,
                    HttpMethod.GET,
                    httpEntity,
                    new ParameterizedTypeReference<>() {});
            return response.getBody();
        } catch (RuntimeException e) {
            log.error("Error: ", e);
            return List.of();
        }
    }

    @Override
    public Optional<Promotion> getPromotion(String productCode) {
        log.info("Fetching promotion from remote service for productCode: {}", productCode);
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<Promotion> response = restTemplate.exchange(
                    properties.promotionServiceUrl() + "/api/promotions/" + productCode,
                    HttpMethod.GET,
                    httpEntity,
                    Promotion.class);
            return Optional.ofNullable(response.getBody());
        } catch (RuntimeException e) {
            log.error("Error: , ", e);
            return Optional.empty();
        }
    }
}
