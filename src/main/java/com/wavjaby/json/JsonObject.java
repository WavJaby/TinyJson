package com.wavjaby.json;

import java.io.Serializable;
import java.util.HashMap;

public class JsonObject extends ValueGetter<JsonObject, String> implements Serializable {
    private final HashMap<String, Object> map = new HashMap<>();

    @SuppressWarnings("unused")
    public JsonObject() {

    }

    public JsonObject(String input) {
        this(new JsonObjectReader(input));
    }

    public JsonObject(JsonObjectReader reader) {
        reader.findJsonStart();
        if (reader.thisChar() == '[') {
            throw new JsonException("JsonObject must start with '['");
        }
        String key = null;
        boolean isValue = false;
        char nextChar;
        while ((nextChar = reader.nextChar()) != '\0') {
            if (nextChar == ',') {
                if (isValue)
                    throw new JsonException("missing value at: " + reader.i);
                nextChar = reader.nextChar();
            }
            if (nextChar == '}') {
                if (isValue)
                    throw new JsonException("missing value at: " + reader.i);
                return;
            }
            switch (nextChar) {
                case '"':
                    if (isValue) {
                        map.put(key, reader.readString());
                        isValue = false;
                    } else
                        key = reader.readString();
                    break;
                case ':':
                    if (!isValue)
                        isValue = true;
                    break;
                case '[':
                    if (isValue) {
                        map.put(key, new JsonArray(reader, false));
                        isValue = false;
                    }
                    break;
                case '{':
                    if (isValue) {
                        map.put(key, new JsonObject(reader));
                        isValue = false;
                    }
                    break;
                default:
                    if (isValue) {
                        map.put(key, reader.readValue());
                        isValue = false;
                    }
            }
        }
        throw new JsonException("JsonObject must end with '}'");
    }

    @SuppressWarnings("unused")
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @SuppressWarnings("unused")
    public boolean notNull(String key) {
        return map.get(key) != null;
    }

    //getter
    @Override
    public Object getObject(String key) {
        return map.get(key);
    }

    //setter
    @SuppressWarnings({"unused"})
    public JsonObject put(String key, Object value) {
        map.put(key, value);
        return this;
    }

    @SuppressWarnings("unused")
    public JsonObject addAll(JsonObject jsonObject) {
        map.putAll(jsonObject.map);
        return this;
    }

    @SuppressWarnings("unused")
    public JsonObject remove(String key) {
        map.remove(key);
        return this;
    }

    /**
     * to string
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (HashMap.Entry<String, Object> item : map.entrySet()) {
            builder.append(",");
            builder.append('\"').append(item.getKey()).append('\"').append(':');
            if (item.getValue() == null)
                builder.append("null");
            else if (item.getValue() instanceof String)
                builder.append('\"').append(item.getValue()).append('\"');
            else
                builder.append(item.getValue());
        }
        if (builder.length() > 0)
            builder.setCharAt(0, '{');
        else
            builder.append('{');
        builder.append('}');
        return builder.toString();
    }

    @SuppressWarnings("unused")
    public String toStringBeauty() {
        return this.toString(1, null);
    }

    String toString(int index, char[] lastTab) {
        StringBuilder builder = new StringBuilder();
        char[] tab = new char[index * 2];
        for (int i = 0; i < index * 2; i++) {
            tab[i] = ' ';
        }
        builder.append('{');
        if (map.size() > 0)
            builder.append('\n');

        int i = map.size();
        for (HashMap.Entry<String, Object> item : map.entrySet()) {
            builder.append(tab).append('\"').append(item.getKey()).append(index < 0 ? "\":" : "\": ");
            Object itemValue = item.getValue();
            if (itemValue == null)
                builder.append("null");
            else if (itemValue instanceof JsonObject)
                builder.append(((JsonObject) itemValue).toString(index + 1, tab));
            else if (itemValue instanceof JsonArray)
                builder.append(((JsonArray) itemValue).toString(index + 1, tab));
            else if (itemValue instanceof String)
                builder.append('\"').append(itemValue).append('\"');
            else
                builder.append(itemValue);

            if (--i > 0)
                builder.append(",");
            builder.append("\n");
        }
        if (lastTab != null && map.size() > 0)
            builder.append(lastTab);
        builder.append("}");

        return builder.toString();
    }
}
