package com.wavjaby.json;

public class JsonBuilder {
    StringBuilder builder = new StringBuilder();
    boolean firstValue = true;

    public JsonBuilder() {
        builder.append('{');
    }

    public JsonBuilder append(String key, String value) {
        if (!firstValue)
            builder.append(',');
        else
            firstValue = false;
        builder.append('"').append(key).append("\":\"").append(value).append('"');
        return this;
    }

    public JsonBuilder append(String key, String value, boolean isJson) {
        if (!firstValue)
            builder.append(',');
        else
            firstValue = false;
        builder.append('"').append(key).append("\":").append(value);
        return this;
    }

    public JsonBuilder append(String key, long value) {
        if (!firstValue)
            builder.append(',');
        else
            firstValue = false;
        builder.append('"').append(key).append("\":").append(value);
        return this;
    }

    public JsonBuilder append(String key, int value) {
        if (!firstValue)
            builder.append(',');
        else
            firstValue = false;
        builder.append('"').append(key).append("\":").append(value);
        return this;
    }

    public JsonBuilder append(String key, boolean value) {
        if (!firstValue)
            builder.append(',');
        else
            firstValue = false;
        builder.append('"').append(key).append("\":").append(value ? "true" : "false");
        return this;
    }

    public JsonBuilder append(String key, JsonBuilder jsonBuilder) {
        if (!firstValue)
            builder.append(',');
        else
            firstValue = false;
        builder.append('"').append(key).append("\":").append(jsonBuilder.toString());
        return this;
    }

    public JsonBuilder append(String key) {
        if (!firstValue)
            builder.append(',');
        else
            firstValue = false;
        builder.append('"').append(key).append("\":null");
        return this;
    }

    @Override
    public String toString() {
        return builder.toString() + '}';
    }
}
