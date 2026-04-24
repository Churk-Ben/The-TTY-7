package com.tty7.core.engine;

public class UnknownBlockException extends RuntimeException {
    public UnknownBlockException(String name) {
        super("Unknown block: " + name);
    }
}
