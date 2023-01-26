package com.wavjaby.json;

import com.wavjaby.json.list.ListedJsonObject;

public class JsonBuilder {
    final StringBuilder builder = new StringBuilder();

    public JsonBuilder append(String key, StringBuilder value) {
        builder.append(',').append('"').append(key).append('"').append(':').append('"').append(value).append('"');
        return this;
    }

    public JsonBuilder append(String key, String value) {
        if (value == null)
            builder.append(',').append('"').append(key).append('"').append(':').append((String) null);
        else
            builder.append(',').append('"').append(key).append('"').append(':').append('"').append(value).append('"');
        return this;
    }

    public JsonBuilder appendRaw(String key, String value) {
        builder.append(',').append('"').append(key).append('"').append(':').append(value);
        return this;
    }

    public JsonBuilder append(String key, long value) {
        builder.append(',').append('"').append(key).append('"').append(':').append(value);
        return this;
    }

    public JsonBuilder append(String key, int value) {
        builder.append(',').append('"').append(key).append('"').append(':').append(value);
        return this;
    }

    public JsonBuilder append(String key, double value) {
        builder.append(',').append('"').append(key).append('"').append(':').append(value);
        return this;
    }

    public JsonBuilder append(String key, float value) {
        builder.append(',').append('"').append(key).append('"').append(':').append(value);
        return this;
    }

    public JsonBuilder append(String key, boolean value) {
        builder.append(',').append('"').append(key).append('"').append(':').append(value);
        return this;
    }

    public JsonBuilder append(String key) {
        builder.append(',').append('"').append(key).append('"').append(':').append((String) null);
        return this;
    }

    public JsonBuilder append(String key, JsonBuilder jsonBuilder) {
        if (jsonBuilder == null) {
            builder.append(',').append('"').append(key).append('"').append(':').append((String) null);
            return this;
        }
        if (jsonBuilder.builder.length() > 0) {
            jsonBuilder.builder.setCharAt(0, '{');
            builder.append(',').append('"').append(key).append('"').append(':').append(jsonBuilder.builder).append('}');
        } else
            builder.append(',').append('"').append(key).append('"').append(':').append('{').append('}');
        return this;
    }

    public JsonBuilder append(String key, JsonArrayBuilder jsonArrayBuilder) {
        if (jsonArrayBuilder == null) {
            builder.append(',').append('"').append(key).append('"').append(':').append((String) null);
            return this;
        }
        if (jsonArrayBuilder.builder.length() > 0) {
            jsonArrayBuilder.builder.setCharAt(0, '[');
            builder.append(',').append('"').append(key).append('"').append(':').append(jsonArrayBuilder.builder).append(']');
        } else
            builder.append(',').append('"').append(key).append('"').append(':').append('[').append(']');
        return this;
    }

    public JsonBuilder append(String key, ListedJsonObject json) {
        if (json == null) {
            builder.append(',').append('"').append(key).append('"').append(':').append((String) null);
            return this;
        }
        builder.append(',').append('"').append(key).append('"').append(':').append(json.toString());
        return this;
    }

    public JsonBuilder append(String key, JsonArray jsonArray) {
        if (jsonArray == null) {
            builder.append(',').append('"').append(key).append('"').append(':').append((String) null);
            return this;
        }
        builder.append(',').append('"').append(key).append('"').append(':').append(jsonArray.toString());
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
}
