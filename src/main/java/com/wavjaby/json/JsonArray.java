package com.wavjaby.json;

import com.wavjaby.json.list.ListedJsonObject;

import java.io.Serializable;
import java.util.Arrays;
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

    public JsonArray(String input, boolean useListedJson) {
        this(new JsonObjectReader(input), useListedJson);

    }

    @SuppressWarnings("unused")
    public JsonArray(String input) {
        this(new JsonObjectReader(input), false);
    }

    public JsonArray(JsonObjectReader reader, boolean useListedJson) {
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
        throw new JsonException("JsonArray must end with ']'");
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

    @SuppressWarnings("unused")
    public JsonArray remove(int index) {
        if (index >= length)
            return this;
        System.arraycopy(items, index + 1, items, index, length - (index + 1));
        items[--length] = null;

        if (length < (items.length >> 2))
            arrayChangeLength(length);
        return this;
    }

    @SuppressWarnings("unused")
    public Object[] toArray() {
        Object[] result = new Object[length];
        System.arraycopy(items, 0, result, 0, length);
        return result;
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
                builder.append('\"').append(item).append('\"');
            else
                builder.append(item);

            if (i < length - 1)
                builder.append(",");
        }
        builder.append(']');
        return builder.toString();
    }

    @SuppressWarnings("unused")
    public String toStringBeauty() {
        return toString(1, null);
    }

    public String toString(int index, char[] lastTab) {
        StringBuilder builder = new StringBuilder();
        char[] tab = new char[index * 2];
        for (int i = 0; i < index * 2; i++) {
            tab[i] = ' ';
        }
        builder.append('[');
        if (length > 0)
            builder.append('\n');

        for (int i = 0; i < length; ++i) {
            Object item = items[i];
            if (item == null)
                builder.append(tab).append("null");
            else if (item instanceof JsonObject)
                builder.append(tab).append(((JsonObject) item).toString(index + 1, tab));
            else if (item instanceof JsonArray)
                builder.append(tab).append(((JsonArray) item).toString(index + 1, tab));
            else if (item instanceof String)
                builder.append(tab).append('\"').append(item).append('\"');
            else if (item instanceof ListedJsonObject)
                builder.append(tab).append(((ListedJsonObject) item).toString(index + 1, tab));
            else
                builder.append(tab).append(item);


            if (i < length - 1)
                builder.append(",");
            builder.append("\n");
        }

        if (lastTab != null && length > 0)
            builder.append(lastTab);
        builder.append("]");

        return builder.toString();
    }

    /**
     * use in foreach
     */
    @Override
    public Iterator<Object> iterator() {
        return Arrays.stream(items, 0, length).iterator();
    }

    @SuppressWarnings("unused")
    public Stream<Object> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}

