package com.wavjaby.json;

public class JsonArrayStringBuilder {
    final StringBuilder builder = new StringBuilder();

    public JsonArrayStringBuilder append(StringBuilder value) {
        builder.append(',').append('"').append(value).append('"');
        return this;
    }

    public JsonArrayStringBuilder append(String value) {
        if (value == null)
            builder.append(',').append((String) null);
        else {
            builder.append(',');
            JsonObject.makeQuote(value, builder);
        }
        return this;
    }

    public JsonArrayStringBuilder appendRaw(String value) {
        builder.append(',').append(value);
        return this;
    }

    public JsonArrayStringBuilder append() {
        builder.append(',').append((String) null);
        return this;
    }

    public JsonArrayStringBuilder append(Number value) {
        builder.append(',').append(value);
        return this;
    }

    public JsonArrayStringBuilder append(JsonObject value) {
        builder.append(',').append(value);
        return this;
    }

    public JsonArrayStringBuilder append(JsonArray value) {
        builder.append(',').append(value);
        return this;
    }

    public JsonArrayStringBuilder append(JsonObjectStringBuilder jsonBuilder) {
        if (jsonBuilder == null) {
            builder.append(',').append((String) null);
            return this;
        }
        if (jsonBuilder.builder.length() > 0) {
            jsonBuilder.builder.setCharAt(0, '{');
            builder.append(',').append(jsonBuilder.builder).append('}');
        } else
            builder.append(',').append('{').append('}');
        return this;
    }

    public JsonArrayStringBuilder append(JsonArrayStringBuilder jsonArrayBuilder) {
        if (jsonArrayBuilder == null) {
            builder.append(',').append((String) null);
            return this;
        }
        if (jsonArrayBuilder.builder.length() > 0) {
            jsonArrayBuilder.builder.setCharAt(0, '[');
            builder.append(',').append(jsonArrayBuilder.builder).append(']');
        } else
            builder.append(',').append('[').append(']');
        return this;
    }

    @Override
    public String toString() {
        if (builder.length() > 0) {
            builder.setCharAt(0, '[');
            builder.append(']');
            String out = builder.toString();
            builder.setLength(builder.length() - 1);
            return out;
        } else
            return "[]";
    }

    public StringBuilder getBuilder() {
        return builder;
    }
}
