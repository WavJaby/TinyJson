package com.wavjaby.json;

import com.wavjaby.json.list.ListedJsonObject;

public class JsonArrayBuilder {
    final StringBuilder builder = new StringBuilder();

    public JsonArrayBuilder append(StringBuilder value) {
        builder.append(',').append('"').append(value).append('"');
        return this;
    }

    public JsonArrayBuilder append(String value) {
        if (value == null)
            builder.append(',').append((String) null);
        else
            builder.append(',').append('"').append(value).append('"');
        return this;
    }

    public JsonArrayBuilder appendRaw(String value) {
        builder.append(',').append(value);
        return this;
    }

    public JsonArrayBuilder append(long value) {
        builder.append(',').append(value);
        return this;
    }

    public JsonArrayBuilder append(int value) {
        builder.append(',').append(value);
        return this;
    }

    public JsonArrayBuilder append(double value) {
        builder.append(',').append(value);
        return this;
    }

    public JsonArrayBuilder append(float value) {
        builder.append(',').append(value);
        return this;
    }

    public JsonArrayBuilder append(boolean value) {
        builder.append(',').append(value);
        return this;
    }

    public JsonArrayBuilder appendNull() {
        builder.append(',').append((String) null);
        return this;
    }

    public JsonArrayBuilder append(JsonBuilder jsonBuilder) {
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

    public JsonArrayBuilder append(JsonArrayBuilder jsonArrayBuilder) {
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

    public JsonArrayBuilder append(ListedJsonObject json) {
        if (json == null) {
            builder.append(',').append((String) null);
            return this;
        }
        builder.append(',').append(json.toString());
        return this;
    }

    public JsonArrayBuilder append(JsonArray jsonArray) {
        if (jsonArray == null) {
            builder.append(',').append((String) null);
            return this;
        }
        builder.append(',').append(jsonArray.toString());
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
}
