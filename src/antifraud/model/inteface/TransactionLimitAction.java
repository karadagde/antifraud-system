package antifraud.model.inteface;

import antifraud.utils.AdjustTransactionLimit;

@FunctionalInterface
public interface TransactionLimitAction {
    void apply(AdjustTransactionLimit adjuster, Long value);

}
