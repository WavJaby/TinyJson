package com.wavjaby.json.list;

import com.wavjaby.json.JsonArray;
import com.wavjaby.json.JsonException;
import com.wavjaby.json.JsonObjectReader;
import com.wavjaby.json.ValueGetter;

import java.io.Serializable;

public class ListedJsonObject extends ValueGetter<ListedJsonObject, String> implements Serializable {
    public final static int DEFAULT_CAPACITY = 10;
    private final Item[] DEFAULT_EMPTY_ARRAY = new Item[0];
    private Item[] items = DEFAULT_EMPTY_ARRAY;
    public int length;
    JsonArray isJsonArray = null;

    @SuppressWarnings("unused")
    public ListedJsonObject() {
        length = 0;
    }

    public ListedJsonObject(String input) {
        this(new JsonObjectReader(input));
    }

    public ListedJsonObject(JsonObjectReader reader) {
        length = 0;
        reader.findJsonStart();
        if (reader.thisChar() == '[') {
            isJsonArray = new JsonArray(reader, true);
            return;
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
                        append(new Item(key, reader.readString()));
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
                        append(new Item(key, new JsonArray(reader, true)));
                        isValue = false;
                    }
                    break;
                case '{':
                    if (isValue) {
                        append(new Item(key, new ListedJsonObject(reader)));
                        isValue = false;
                    }
                    break;
                default:
                    if (isValue) {
                        append(new Item(key, reader.readValue()));
                        isValue = false;
                    }
            }
        }
        throw new JsonException("JsonObject must end with '}'");
    }

    @SuppressWarnings("unused")
    public boolean containsKey(String key) {
        return indexOf(key) > -1;
    }

    @SuppressWarnings("unused")
    public boolean notNull(String key) {
        return getObject(key) != null;
    }

    //getter
    @Override
    public Object getObject(String key) {
        if (isJsonArray != null)
            throw new JsonException("This is JsonArray, use \"toJsonArray\" to get it");

        int pos = indexOf(key);
        if (pos > -1)
            return items[pos].value;
        else
            return null;
    }

    @SuppressWarnings("unused")
    public Item getItem(String key) {
        int pos = indexOf(key);
        if (pos > -1)
            return items[pos];
        else
            return null;
    }

    @SuppressWarnings("unused")
    public boolean isJsonArray() {
        return isJsonArray != null;
    }

    @SuppressWarnings("unused")
    public JsonArray toJsonArray() {
        if (isJsonArray == null)
            throw new JsonException("This is not an JsonArray");
        return isJsonArray;
    }

    public Item[] Items() {
        Item[] out = new Item[length];
        System.arraycopy(items, 0, out, 0, length);
        return out;
    }

    //setter
    @SuppressWarnings({"unused", "UnusedReturnValue"})
    public ListedJsonObject put(String key, Object value) {
        int pos = indexOf(key);
        if (pos > -1)
            items[pos].setValue(value);
        else
            append(new Item(key, value));
        return this;
    }

    @SuppressWarnings("unused")
    public JsonArray remove(int index) {
        if (isJsonArray != null) {
            isJsonArray.remove(index);
            return isJsonArray;
        } else
            return null;
    }

    @SuppressWarnings("unused")
    public ListedJsonObject addAll(ListedJsonObject listedJsonObject) {
        if (isJsonArray == null) {
            for (Item i : listedJsonObject.Items()) {
                put(i.key, i.value);
            }
            return this;
        }
        throw new JsonException("This is JsonArray, use \"toJsonArray\" to get it");
    }

    @SuppressWarnings("unused")
    public ListedJsonObject remove(String key) {
        int index;
        if ((index = indexOf(key)) == -1)
            return this;
        System.arraycopy(items, index + 1, items, index, length - (index + 1));
        items[--length] = null;
        if (length < (items.length >> 2))
            arrayChangeLength(length);
        return this;
    }

    private void append(Item item) {
        if (this.items.length == length) {
            int newLength = items.length == 0
                    ? DEFAULT_CAPACITY
                    : items.length + (items.length >> 1);
            arrayChangeLength(newLength);
        }
        this.items[length] = item;
        length++;
    }

    /**
     * extend array
     */
    private void arrayChangeLength(int newLength) {
        if (newLength == 0)
            this.items = DEFAULT_EMPTY_ARRAY;
        int preserveLength = Math.max(DEFAULT_CAPACITY, newLength);
        if (preserveLength != this.items.length) {
            Item[] copy = new Item[preserveLength];
            System.arraycopy(this.items, 0, copy, 0, length);
            this.items = copy;
        }
    }

    /**
     * find key index
     */

    public int indexOf(String key) {
        for (int i = 0; i < length; i++) {
            if (items[i].key.equals(key))
                return i;
        }
        return -1;
    }

    /**
     * to string
     */
    @Override
    public String toString() {
        if (this.isJsonArray != null)
            return this.isJsonArray.toString();

        StringBuilder builder = new StringBuilder();
        builder.append('{');
        for (int i = 0; i < length; ++i) {
            Item item = this.items[i];
            builder.append('\"').append(item.key).append('\"').append(':');
            if (item.value == null)
                builder.append("null");
            else if (item.value instanceof String)
                builder.append('\"').append(item.value).append('\"');
            else
                builder.append(item.value);

            if (i < length - 1)
                builder.append(",");
        }
        builder.append('}');
        return builder.toString();
    }

    @SuppressWarnings("unused")
    public String toStringBeauty() {
        return this.isJsonArray != null ? this.isJsonArray.toStringBeauty() : this.toString(1, null);
    }

    public String toString(int index, char[] lastTab) {
        StringBuilder builder = new StringBuilder();
        char[] tab = new char[index * 2];
        for (int i = 0; i < index * 2; i++) {
            tab[i] = ' ';
        }
        builder.append('{');
        if (length > 0)
            builder.append('\n');

        for (int i = 0; i < this.length; ++i) {
            Item item = this.items[i];
            builder.append(tab).append('\"').append(item.key).append(index < 0 ? "\":" : "\": ");
            if (item.value == null)
                builder.append("null");
            else if (item.value instanceof ListedJsonObject)
                builder.append(((ListedJsonObject) item.value).toString(index + 1, tab));
            else if (item.value instanceof JsonArray)
                builder.append(((JsonArray) item.value).toString(index + 1, tab));
            else if (item.value instanceof String)
                builder.append('\"').append(item.value).append('\"');
            else
                builder.append(item.value);

            if (i < length - 1)
                builder.append(",");
            builder.append("\n");
        }
        if (lastTab != null && length > 0)
            builder.append(lastTab);
        builder.append("}");

        return builder.toString();
    }
}
