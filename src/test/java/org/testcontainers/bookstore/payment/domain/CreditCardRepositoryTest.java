package org.testcontainers.bookstore.payment.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.bookstore.payment.domain.CreditCard;
import org.testcontainers.bookstore.payment.domain.CreditCardRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:tc:postgresql:15-alpine:///dbname"
})
class CreditCardRepositoryTest {
    @Autowired
    private CreditCardRepository creditCardRepository;

    @Test
    void shouldGetCreditCardByCardNumber() {
        Optional<CreditCard> optionalCreditCard = creditCardRepository.findByCardNumber("1111222233334444");
        assertThat(optionalCreditCard).isNotEmpty();
        assertThat(optionalCreditCard.get().getCardNumber()).isEqualTo("1111222233334444");
        assertThat(optionalCreditCard.get().getCvv()).isEqualTo("123");
        assertThat(optionalCreditCard.get().getExpiryMonth()).isEqualTo(2);
        assertThat(optionalCreditCard.get().getExpiryYear()).isEqualTo(2030);
    }

    @Test
    void shouldReturnEmptyWhenCardNumberNotFound() {
        Optional<CreditCard> optionalCreditCard = creditCardRepository.findByCardNumber("1111111111111");
        assertThat(optionalCreditCard).isEmpty();
    }
}