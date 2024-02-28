package antifraud.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TransactionLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 16, unique = true, updatable = false, nullable = false)
    private String cardNumber;

    @Column(nullable = false)
    @Min(1)
    private Long maxAllowed = 200L;

    @Column(nullable = false)
    @Min(1)
    private Long maxManual = 1500L;


}
