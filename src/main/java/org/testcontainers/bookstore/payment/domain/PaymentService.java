package org.testcontainers.bookstore.payment.domain;

import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final CreditCardRepository creditCardRepository;

    public PaymentService(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    public PaymentResponse authorize(PaymentRequest request) {
        Optional<CreditCard> creditCardOptional = creditCardRepository.findByCardNumber(request.getCardNumber());
        if (creditCardOptional.isEmpty()) {
            return new PaymentResponse(PaymentResponse.PaymentStatus.REJECTED);
        }
        CreditCard creditCard = creditCardOptional.get();
        if (creditCard.getCvv().equals(request.getCvv())
                && creditCard.getExpiryMonth() == request.getExpiryMonth()
                && creditCard.getExpiryYear() == request.getExpiryYear()) {
            return new PaymentResponse(PaymentResponse.PaymentStatus.ACCEPTED);
        }
        return new PaymentResponse(PaymentResponse.PaymentStatus.REJECTED);
    }
}
