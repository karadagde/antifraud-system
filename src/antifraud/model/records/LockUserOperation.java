package antifraud.model.records;

import antifraud.model.enums.Operation;

public record LockUserOperation(String username, Operation operation) {
}
