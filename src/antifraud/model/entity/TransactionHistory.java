package antifraud.model.entity;

import antifraud.model.enums.Region;
import antifraud.model.enums.TransactionFeedback;
import antifraud.model.enums.TransactionResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private Long amount;
    private String ip;
    private String number;
    @Enumerated(EnumType.STRING)
    private Region region;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")

    private Date date;

    @Enumerated(EnumType.STRING)
    private TransactionResult result;

    @Enumerated(EnumType.STRING)
    private TransactionFeedback feedback;

    private boolean feedBackProvided = false;


}
