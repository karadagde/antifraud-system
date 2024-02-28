package antifraud.repository;

import antifraud.model.entity.TransactionHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.List;


public interface TransactionRepository extends CrudRepository<TransactionHistory, Long> {


    @Query(value = "SELECT COUNT(DISTINCT region) FROM transaction_history WHERE number= :number AND region != :region AND date > :date AND date <= :tDate", nativeQuery = true)
    Long filterTransactionByRegion(@Param("number") String number, @Param("region") String region, @Param("date") Date date, @Param("tDate") Date tDate);

    @Query(value = "SELECT COUNT(DISTINCT ip) FROM transaction_history WHERE number= :number AND ip != :ip AND date > :date AND date <= :tDate", nativeQuery = true)
    Long filterTransactionByIp(@Param("number") String number, @Param("ip") String ip, @Param("date") Date date, @Param("tDate") Date tDate);

    List<TransactionHistory> getAllByNumber(String number);

    @Override
    @NonNull
    @Query(value = "SELECT * FROM transaction_history", nativeQuery = true)
    List<TransactionHistory> findAll();

}
