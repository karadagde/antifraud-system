package antifraud.model.dto;

import antifraud.model.entity.TransactionHistory;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter

public class TransactionHistoryResponseDTO {
    private final Long transactionId;
    private Long amount;
    private String ip;
    private String number;
    private String region;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date date;

    private String result;
    private String feedback;

    public TransactionHistoryResponseDTO(TransactionHistory transactionHistory) {
        this.transactionId = transactionHistory.getTransactionId();
        this.amount = transactionHistory.getAmount();
        this.ip = transactionHistory.getIp();
        this.number = transactionHistory.getNumber();
        this.region = String.valueOf(transactionHistory.getRegion());
        this.date = transactionHistory.getDate();
        this.result = transactionHistory.getResult() != null ? String.valueOf(transactionHistory.getResult()) : "";
        this.feedback = transactionHistory.getFeedback() != null ? String.valueOf(transactionHistory.getFeedback()) : "";
    }


}
