package antifraud.repository;

import antifraud.model.entity.TransactionLimit;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TransactionLimitRepository extends CrudRepository<TransactionLimit, Long> {

    Optional<TransactionLimit> findTransactionLimitByCardNumber(String cardNumber);


}
