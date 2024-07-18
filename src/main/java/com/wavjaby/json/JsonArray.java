package com.wavjaby.json;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JsonArray extends ValueGetter<JsonArray, Integer> implements Serializable, Iterable<Object> {
    public final static int DEFAULT_CAPACITY = 10;
    private final Object[] DEFAULT_EMPTY_ARRAY = new Object[0];
    private Object[] items = DEFAULT_EMPTY_ARRAY;
    public int length;

    @SuppressWarnings("unused")
    public JsonArray() {
        length = 0;
    }

    public JsonArray(InputStream input) {
        this(new JsonReader(input, StandardCharsets.UTF_8), false);
    }

    public JsonArray(InputStream input, Charset charset) {
        this(new JsonReader(input, charset), false);
    }

    @SuppressWarnings("unused")
    public JsonArray(String input, boolean useListedJson) {
        this(new JsonReader(input), useListedJson);
    }

    @SuppressWarnings("unused")
    public JsonArray(String input) {
        this(new JsonReader(input), false);
    }

    public JsonArray(JsonReader reader, boolean useListedJson) {
        this.length = 0;
        reader.findArrayStart();
        char nextChar;
        while ((nextChar = reader.nextChar()) != '\0') {
            if (nextChar == ',')
                nextChar = reader.nextChar();
            if (nextChar == ']')
                return;
            switch (nextChar) {
                case '"':
                    add(reader.readString());
                    break;
                case '[':
                    add(new JsonArray(reader, useListedJson));
                    break;
                case '{':
                    add(useListedJson ? new ListedJsonObject(reader) : new JsonObject(reader));
                    break;
                default:
                    add(reader.readValue());
            }
        }
        throw new JsonException("JsonArray must end with ']',", reader);
    }

    public JsonArray(Collection<?> collection) {
        length = collection.size();
        items = new Object[length];
        int i = 0;
        for (Object o : collection)
            items[i++] = JsonObject.warpValue(o);
    }

    @Override
    public Object getObject(Integer index) {
        if (index < length)
            return items[index];
        throw new IndexOutOfBoundsException();
    }

    //setter
    @SuppressWarnings("unused")
    public JsonArray add(Object item) {
        if (items.length <= length) {
            int newLength = items.length == 0
                    ? DEFAULT_CAPACITY
                    : items.length + (items.length >> 1);
            arrayChangeLength(newLength);
        }
        items[length] = item;
        length++;
        return this;
    }

    @SuppressWarnings("unused")
    public JsonArray addAll(JsonArray jsonArray) {
        for (int i = 0; i < jsonArray.items.length; i++)
            add(jsonArray.items[i]);
        return this;
    }

    @SuppressWarnings("unused")
    public JsonArray set(int index, Object value) {
        if (index < length)
            items[index] = value;
        else
            throw new IndexOutOfBoundsException();
        return this;
    }

    @SuppressWarnings({"unused", "UnusedReturnValue"})
    public JsonArray remove(int index) {
        if (index >= length)
            return this;
        System.arraycopy(items, index + 1, items, index, length - (index + 1));
        items[--length] = null;

        if (length < (items.length >> 2))
            arrayChangeLength(length);
        return this;
    }

    /**
     * extend array
     */
    private void arrayChangeLength(int newLength) {
        if (newLength == 0)
            this.items = DEFAULT_EMPTY_ARRAY;
        int preserveLength = Math.max(DEFAULT_CAPACITY, newLength);
        if (preserveLength != this.items.length) {
            Object[] copy = new Object[preserveLength];
            System.arraycopy(this.items, 0, copy, 0, length);
            this.items = copy;
        }
    }

    /**
     * find key index
     */
    @SuppressWarnings("unused")
    public boolean content(Object value) {
        return indexOf(value) != -1;
    }


    public int indexOf(Object obj) {
        if (obj == null) {
            for (int i = 0; i < length; i++) {
                if (items[i] == null)
                    return i;
            }
        } else {
            for (int i = 0; i < length; i++) {
                if (obj.equals(items[i]))
                    return i;
            }
        }
        return -1;
    }

    /**
     * to string
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < length; ++i) {
            Object item = items[i];
            if (item == null)
                builder.append("null");
            else if (item instanceof String)
                JsonObject.makeQuote((String) item, builder);
            else
                builder.append(item);

            if (i < length - 1)
                builder.append(',');
        }
        builder.append(']');
        return builder.toString();
    }

    @SuppressWarnings("unused")
    public String toStringBeauty() {
        return toString(4, 1, null);
    }

    String toString(int tabSize, int index, char[] lastTab) {
        StringBuilder builder = new StringBuilder();
        char[] tab = JsonObject.createTab(tabSize, index, lastTab);

        builder.append('[');
        if (length > 0)
            builder.append('\n');

        for (int i = 0; i < length; ++i) {
            builder.append(tab);
            JsonObject.appendValue(items[i], i < length - 1, tabSize, index, tab, builder);
        }

        if (lastTab != null && length > 0)
            builder.append(lastTab);
        builder.append(']');

        return builder.toString();
    }

    /**
     * Iterator
     */
    @Override
    public Iterator<Object> iterator() {
        return Arrays.stream(items, 0, length).iterator();
    }

    @SuppressWarnings("unused")
    public Stream<Object> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @SuppressWarnings("unused")
    public Object[] toArray() {
        Object[] result = new Object[length];
        System.arraycopy(items, 0, result, 0, length);
        return result;
    }
}

