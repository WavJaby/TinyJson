package com.wavjaby.json;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonObject extends ValueGetter<JsonObject, String> implements Serializable, Iterable<Map.Entry<String, Object>> {
    private final HashMap<String, Object> map;

    @SuppressWarnings("unused")
    public JsonObject() {
        this.map = new HashMap<>();
    }

    public JsonObject(String input) {
        this(new JsonReader(input));
    }

    public JsonObject(InputStream input) {
        this(new JsonReader(input, StandardCharsets.UTF_8));
    }

    public JsonObject(InputStream input, Charset charset) {
        this(new JsonReader(input, charset));
    }

    public JsonObject(JsonReader reader) {
        this.map = new HashMap<>();
        reader.findJsonStart();

        if (reader.thisChar() != '{') {
            throw new JsonException("JsonObject must start with '{',", reader);
        }
        String key = null;
        boolean isValue = false;
        char nextChar;
        while ((nextChar = reader.nextChar()) != '\0') {
            if (nextChar == ',') {
                if (isValue)
                    throw new JsonException("Missing value", reader);
                nextChar = reader.nextChar();
            }
            if (nextChar == '}') {
                if (isValue)
                    throw new JsonException("Missing value", reader);
                return;
            }
            switch (nextChar) {
                case '"':
                    if (isValue) {
                        map.put(key, reader.readString());
                        key = null;
                        isValue = false;
                    } else if (key == null)
                        key = reader.readString();
                    else
                        throw new JsonException("Missing ':' after a key", reader);
                    break;
                case ':':
                    if (!isValue)
                        isValue = true;
                    break;
                case '[':
                    if (isValue) {
                        map.put(key, new JsonArray(reader, false));
                        key = null;
                        isValue = false;
                    }
                    break;
                case '{':
                    if (isValue) {
                        map.put(key, new JsonObject(reader));
                        key = null;
                        isValue = false;
                    }
                    break;
                default:
                    if (isValue) {
                        map.put(key, reader.readValue());
                        key = null;
                        isValue = false;
                    } else
                        throw new JsonException("Missing ':' after a key", reader);
            }
        }
        throw new JsonException("JsonObject must end with '}',", reader);
    }

    public JsonObject(Map<?,?> map) {
        if (map == null)
            this.map = new HashMap<>();
        else {
            this.map = new HashMap<>(map.size());
            for (final Map.Entry<?, ?> e : map.entrySet()) {
                if(e.getKey() == null)
                    throw new NullPointerException("Null key");
                final Object value = e.getValue();
                if (value != null)
                    this.map.put(String.valueOf(e.getKey()), warpValue(value));
            }
        }
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
            makeQuote(item.getKey(), builder);
            builder.append(':');
            if (item.getValue() == null)
                builder.append("null");
            else if (item.getValue() instanceof String)
                makeQuote((String) item.getValue(), builder);
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
        return this.toString(4, 1, null);
    }

    String toString(int tabSize, int index, char[] lastTab) {
        StringBuilder builder = new StringBuilder();
        char[] tab = JsonObject.createTab(tabSize, index, lastTab);
        builder.append('{');
        if (map.size() > 0)
            builder.append('\n');

        int i = map.size();
        for (HashMap.Entry<String, Object> item : map.entrySet()) {
            builder.append(tab);
            makeQuote(item.getKey(), builder);
            builder.append(": ");
            appendValue(item.getValue(), --i > 0, tabSize, index, tab, builder);
        }

        if (lastTab != null && map.size() > 0)
            builder.append(lastTab);
        builder.append('}');

        return builder.toString();
    }

    static void appendValue(Object value, boolean addComma, int tabSize, int index, char[] tab, StringBuilder builder) {
        if (value == null)
            builder.append("null");
        else if (value instanceof JsonObject)
            builder.append(((JsonObject) value).toString(tabSize, index + 1, tab));
        else if (value instanceof JsonArray)
            builder.append(((JsonArray) value).toString(tabSize, index + 1, tab));
        else if (value instanceof ListedJsonObject)
            builder.append(((ListedJsonObject) value).toString(tabSize, index + 1, tab));
        else if (value instanceof String)
            makeQuote((String) value, builder);
        else if (value instanceof Collection)
            builder.append(new JsonArray((Collection<?>) value));
        else if (value instanceof Map)
            builder.append(new JsonObject((Map<?, ?>) value));
        else
            builder.append(value);
        if (addComma)
            builder.append(",");
        builder.append("\n");
    }

    static Object warpValue(Object value) {
        if(value == null) return null;
        if (value instanceof Collection)
            return new JsonArray((Collection<?>) value);
        else if (value instanceof Map)
            return new JsonObject((Map<?, ?>) value);
        else
            return value;
    }

    static char[] createTab(int tabSize, int index, char[] lastTab) {
        char[] tab;
        if (lastTab == null) {
            tab = new char[tabSize];
        } else {
            tab = new char[index * tabSize];
            System.arraycopy(lastTab, 0, tab, 0, lastTab.length);
        }
        for (int i = lastTab == null ? 0 : lastTab.length; i < tab.length; i++)
            tab[i] = ' ';
        return tab;
    }

    public static void makeQuote(String string, StringBuilder builder) {
        if (string.isEmpty()) {
            builder.append("\"\"");
            return;
        }

        char lastChar;
        char thisChar = 0;
        String unicode;
        int len = string.length();

        builder.append('"');
        for (int i = 0; i < len; i++) {
            lastChar = thisChar;
            thisChar = string.charAt(i);
            switch (thisChar) {
                case '\\':
                case '"':
                    builder.append('\\');
                    builder.append(thisChar);
                    break;
                case '/':
                    if (lastChar == '<')
                        builder.append('\\');
                    builder.append(thisChar);
                    break;
                case '\b':
                    builder.append("\\b");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\f':
                    builder.append("\\f");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                default:
                    if (thisChar < ' ' || (thisChar >= '\u0080' && thisChar < '\u00a0') || (thisChar >= '\u2000' && thisChar < '\u2100')) {
                        builder.append("\\u");
                        unicode = Integer.toHexString(thisChar);
                        builder.append("0000", 0, 4 - unicode.length());
                        builder.append(unicode);
                    } else
                        builder.append(thisChar);
            }
        }
        builder.append('"');
    }

    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return map.entrySet().iterator();
    }

    public HashMap<String, Object> getMap() {
        return map;
    }
}
