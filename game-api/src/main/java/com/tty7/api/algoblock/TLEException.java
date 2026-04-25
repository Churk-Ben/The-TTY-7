package com.tty7.api.algoblock;

public class TLEException extends RuntimeException {
    public TLEException() {
        super("Step budget exceeded");
    }
}
