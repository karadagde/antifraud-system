package antifraud.utils;

import antifraud.model.enums.TransactionFeedback;
import antifraud.model.enums.TransactionResult;
import antifraud.model.inteface.TransactionLimitAction;

import java.util.EnumMap;

public class DecisionMap {

    private EnumMap<TransactionResult, EnumMap<TransactionFeedback, TransactionLimitAction>> matrix;

    public DecisionMap() {
        initializeMatrix();
    }

    private void initializeMatrix() {
        matrix = new EnumMap<>(TransactionResult.class);

        for (TransactionResult result : TransactionResult.values()) {

            EnumMap<TransactionFeedback, TransactionLimitAction> feedbackMap = new EnumMap<>(TransactionFeedback.class);

            for (TransactionFeedback feedback : TransactionFeedback.values()) {
                feedbackMap.put(feedback, ((adjuster, value) -> {
                }));
            }

            matrix.put(result, feedbackMap);
        }

        matrix.get(TransactionResult.ALLOWED).put(TransactionFeedback.ALLOWED, ((adjuster, value) -> {
            adjuster.exceptionCase();
        }));
        matrix.get(TransactionResult.ALLOWED).put(TransactionFeedback.MANUAL_PROCESSING, (AdjustTransactionLimit::decreaseMaxAllowed));
        matrix.get(TransactionResult.ALLOWED).put(TransactionFeedback.PROHIBITED, (AdjustTransactionLimit::decreaseBoth));


        matrix.get(TransactionResult.MANUAL_PROCESSING).put(TransactionFeedback.ALLOWED, (AdjustTransactionLimit::increaseMaxAllowed));
        matrix.get(TransactionResult.MANUAL_PROCESSING).put(TransactionFeedback.MANUAL_PROCESSING, ((adjuster, value) -> {
            adjuster.exceptionCase();
        }));
        matrix.get(TransactionResult.MANUAL_PROCESSING).put(TransactionFeedback.PROHIBITED, (AdjustTransactionLimit::decreaseMaxManual));

        matrix.get(TransactionResult.PROHIBITED).put(TransactionFeedback.ALLOWED, (AdjustTransactionLimit::increaseBoth));
        matrix.get(TransactionResult.PROHIBITED).put(TransactionFeedback.MANUAL_PROCESSING, ((adjuster, value) -> {
            adjuster.increaseMaxManual(value);
            // This is the regular syntax for lambda expression
            // I keep it here to show the difference between the two
        }));
        matrix.get(TransactionResult.PROHIBITED).put(TransactionFeedback.PROHIBITED, ((adjuster, value) -> {
            adjuster.exceptionCase();
        }));

    }


    public TransactionLimitAction getAction(TransactionResult result, TransactionFeedback feedback) {
        return matrix.getOrDefault(result, new EnumMap<>(TransactionFeedback.class)).get(feedback);
    }
}
