package com.tty7.api;

public class TLEException extends RuntimeException {
    public TLEException() {
        super("Step budget exceeded");
    }
}
