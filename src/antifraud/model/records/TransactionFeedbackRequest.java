package antifraud.model.records;

import antifraud.model.enums.TransactionFeedback;
import jakarta.validation.constraints.NotNull;


public record TransactionFeedbackRequest(@NotNull Long transactionId, @NotNull TransactionFeedback feedback) {
}
