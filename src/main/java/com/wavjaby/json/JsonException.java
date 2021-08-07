package com.wavjaby.json;

public class JsonException extends RuntimeException {
    public JsonException(final String message, Throwable e) {
        super(message, e);
    }

    public JsonException(final String message) {
        super(message);
    }
}
