package com.tty7.api.algoblock;

import com.tty7.api.algoblock.ValidationResult;

public record ValidationResult(boolean valid, String message) {
    public static final ValidationResult OK = new ValidationResult(true, "OK");

    public static ValidationResult error(String message) {
        return new ValidationResult(false, message);
    }
}
