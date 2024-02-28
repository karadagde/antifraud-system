package antifraud.utils;


public class AdjustTransactionLimit {

    public Long maxAllowed;
    public Long maxManual;
    public boolean exceptionCase = false;

    public AdjustTransactionLimit(Long maxAllowed, Long maxManual) {
        this.maxManual = maxManual;
        this.maxAllowed = maxAllowed;
    }

    public void increaseBoth(Long val) {
        increaseMaxAllowed(val);
        increaseMaxManual(val);
    }

    public void increaseMaxAllowed(Long value) {
        this.maxAllowed = increaseLimit(this.maxAllowed, value);

    }

    public void increaseMaxManual(Long value) {
        this.maxManual = increaseLimit(this.maxManual, value);

    }

    private Long increaseLimit(Long current, Long transaction_val) {
        return (long) Math.ceil(0.8 * current + 0.2 * transaction_val);
    }

    public void decreaseBoth(Long val) {
        decreaseMaxAllowed(val);
        decreaseMaxManual(val);
    }

    public void decreaseMaxAllowed(Long value) {
        this.maxAllowed = decreaseLimit(this.maxAllowed, value);
    }

    public void decreaseMaxManual(Long value) {
        this.maxManual = decreaseLimit(this.maxManual, value);
    }

    private Long decreaseLimit(Long current, Long transaction_val) {
        return (long) Math.ceil(0.8 * current - 0.2 * transaction_val);
    }

    public void exceptionCase() {
        this.exceptionCase = true;

    }

    public AdjustTransactionLimit getValues() {
        return this;
    }

}
