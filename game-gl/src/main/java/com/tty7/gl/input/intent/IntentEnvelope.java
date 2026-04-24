package com.tty7.gl.input.intent;

public record IntentEnvelope(InputIntent intent, long createdAtMillis, Long expireAtMillis) {
    public boolean isExpired(long nowMillis) {
        return expireAtMillis != null && nowMillis > expireAtMillis;
    }
}
