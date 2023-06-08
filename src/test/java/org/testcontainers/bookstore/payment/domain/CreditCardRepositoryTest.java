package org.testcontainers.bookstore.payment.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {"spring.datasource.url=jdbc:tc:postgresql:15-alpine:///dbname"})
class CreditCardRepositoryTest {
    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        creditCardRepository.deleteAllInBatch();

        entityManager.persist(new CreditCard(null, "John", "1111222233334444", "123", 2, 2030));
        entityManager.persist(new CreditCard(null, "Siva", "1234123412341234", "123", 10, 2030));
        entityManager.persist(new CreditCard(null, "Kevin", "1234567890123456", "123", 3, 2030));
    }

    @Test
    void shouldGetAllCreditCards() {
        List<CreditCard> creditCards = creditCardRepository.findAll();
        assertThat(creditCards).hasSize(3);
    }

    @ParameterizedTest
    @CsvSource({"1111222233334444, 123, 2, 2030", "1234123412341234, 123, 10, 2030", "1234567890123456, 123, 3, 2030"})
    void shouldGetCreditCardByCardNumber(String cardNumber, String cvv, int expiryMonth, int expiryYear) {
        Optional<CreditCard> optionalCreditCard = creditCardRepository.findByCardNumber(cardNumber);
        assertThat(optionalCreditCard).isNotEmpty();
        assertThat(optionalCreditCard.get().getCardNumber()).isEqualTo(cardNumber);
        assertThat(optionalCreditCard.get().getCvv()).isEqualTo(cvv);
        assertThat(optionalCreditCard.get().getExpiryMonth()).isEqualTo(expiryMonth);
        assertThat(optionalCreditCard.get().getExpiryYear()).isEqualTo(expiryYear);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1111111111111111", "2222222222222222", "3333333333333333"})
    void shouldReturnEmptyWhenCardNumberNotFound(String cardNumber) {
        Optional<CreditCard> optionalCreditCard = creditCardRepository.findByCardNumber(cardNumber);
        assertThat(optionalCreditCard).isEmpty();
    }
}
