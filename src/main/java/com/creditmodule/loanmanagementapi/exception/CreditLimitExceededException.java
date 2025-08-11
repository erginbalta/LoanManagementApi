package com.creditmodule.loanmanagementapi.exception;

public class CreditLimitExceededException extends RuntimeException {
    public CreditLimitExceededException(String message) {
        super(message);
    }
}

