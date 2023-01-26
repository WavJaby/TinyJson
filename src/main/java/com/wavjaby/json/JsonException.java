package com.wavjaby.json;

public class JsonException extends RuntimeException {
    public JsonException(final String message, final Throwable e) {
        super(message, e);
    }

    public JsonException(final String message) {
        super(message);
    }

    public JsonException(final String message, final int line, final int index) {
        super(message + " at " + line + ":" + index);
    }
}
