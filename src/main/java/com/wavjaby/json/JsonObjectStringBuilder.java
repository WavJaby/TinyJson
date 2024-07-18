package com.wavjaby.json;

public class JsonObjectStringBuilder {
    final StringBuilder builder = new StringBuilder();

    public JsonObjectStringBuilder append(String key, StringBuilder value) {
        builder.append(',').append('"').append(key).append('"').append(':').append('"').append(value).append('"');
        return this;
    }

    public JsonObjectStringBuilder append(String key, String value) {
        if (value == null)
            builder.append(',').append('"').append(key).append('"').append(':').append((String) null);
        else {
            builder.append(',').append('"').append(key).append('"').append(':');
            JsonObject.makeQuote(value, builder);
        }
        return this;
    }

    public JsonObjectStringBuilder appendRaw(String key, String value) {
        builder.append(',').append('"').append(key).append('"').append(':').append(value);
        return this;
    }

    public JsonObjectStringBuilder append(String key) {
        builder.append(',').append('"').append(key).append('"').append(':').append((String) null);
        return this;
    }

    public JsonObjectStringBuilder append(String key, Number value) {
        builder.append(',').append('"').append(key).append('"').append(':').append(value);
        return this;
    }

    public JsonObjectStringBuilder append(String key, boolean value) {
        builder.append(',').append('"').append(key).append('"').append(':').append(value);
        return this;
    }

    public JsonObjectStringBuilder append(String key, JsonObject value) {
        builder.append(',').append('"').append(key).append('"').append(':').append(value);
        return this;
    }

    public JsonObjectStringBuilder append(String key, JsonArray value) {
        builder.append(',').append('"').append(key).append('"').append(':').append(value);
        return this;
    }

    public JsonObjectStringBuilder append(String key, JsonObjectStringBuilder jsonBuilder) {
        builder.append(',').append('"').append(key).append('"').append(':');
        if (jsonBuilder == null) {
            builder.append((String) null);
            return this;
        }
        if (jsonBuilder.builder.length() > 0) {
            jsonBuilder.builder.setCharAt(0, '{');
            builder.append(jsonBuilder.builder).append('}');
        } else
            builder.append('{').append('}');
        return this;
    }

    public JsonObjectStringBuilder append(String key, JsonArrayStringBuilder jsonArrayBuilder) {
        builder.append(',').append('"').append(key).append('"').append(':');
        if (jsonArrayBuilder == null) {
            builder.append((String) null);
            return this;
        }
        if (jsonArrayBuilder.builder.length() > 0) {
            jsonArrayBuilder.builder.setCharAt(0, '[');
            builder.append(jsonArrayBuilder.builder).append(']');
        } else
            builder.append('[').append(']');
        return this;
    }

    @Override
    public String toString() {
        if (builder.length() > 0) {
            builder.setCharAt(0, '{');
            builder.append('}');
            String out = builder.toString();
            builder.setLength(builder.length() - 1);
            return out;
        } else
            return "{}";
    }

    public StringBuilder getBuilder() {
        return builder;
    }
}
