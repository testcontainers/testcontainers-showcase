package org.testcontainers.bookstore.catalog.clients.promotions;

import java.util.List;
import java.util.Optional;

public interface PromotionServiceClient {
    List<Promotion> getPromotions(List<String> productCodes);

    Optional<Promotion> getPromotion(String productCode);
}
