package antifraud.repository;

import antifraud.model.entity.CreditCard;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CreditCardRepository extends CrudRepository<CreditCard, Integer> {
    Optional<CreditCard> findCreditCardByNumber(String cardNumber);
}
