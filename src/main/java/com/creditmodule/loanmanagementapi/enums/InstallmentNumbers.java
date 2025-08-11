package com.creditmodule.loanmanagementapi.enums;

public enum InstallmentNumbers {
    SIX(6),
    NINE(9),
    TWELVE(12),
    TWENTY_FOUR(24);

    private final int value;

    InstallmentNumbers(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}