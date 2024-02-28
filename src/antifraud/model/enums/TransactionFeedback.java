package antifraud.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)

public enum TransactionFeedback {
    ALLOWED, PROHIBITED, MANUAL_PROCESSING

}
