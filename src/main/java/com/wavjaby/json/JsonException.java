package com.wavjaby.json;

public class JsonException extends RuntimeException {
    public JsonException(final String message, final Throwable e) {
        super(message, e);
    }

    public JsonException(final String message) {
        super(message);
    }

    public JsonException(final String message, final JsonReader reader) {
        super(message + reader.createPart());
    }
}
