package com.wavjaby.json;

import java.io.Serializable;

public class ListedJsonObject extends ValueGetter<ListedJsonObject, String> implements Serializable {
    public final static int DEFAULT_CAPACITY = 10;
    private final Item[] DEFAULT_EMPTY_ARRAY = new Item[0];
    private Item[] items = DEFAULT_EMPTY_ARRAY;
    public int length;
    JsonArray isJsonArray = null;

    public static class Item implements Serializable {
        final String key;
        Object value;

        Item(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        @SuppressWarnings("unused")
        public String getKey() {
            return key;
        }

        @SuppressWarnings("unused")
        public Object getValue() {
            return value;
        }

        void setValue(Object value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return key + ": " + value;
        }
    }

    @SuppressWarnings("unused")
    public ListedJsonObject() {
        length = 0;
    }

    public ListedJsonObject(String input) {
        this(new JsonReader(input));
    }

    public ListedJsonObject(JsonReader reader) {
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
                        append(new Item(key, reader.readString()));
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
                        append(new Item(key, new JsonArray(reader, true)));
                        key = null;
                        isValue = false;
                    }
                    break;
                case '{':
                    if (isValue) {
                        append(new Item(key, new ListedJsonObject(reader)));
                        key = null;
                        isValue = false;
                    }
                    break;
                default:
                    if (isValue) {
                        append(new Item(key, reader.readValue()));
                        key = null;
                        isValue = false;
                    }
            }
        }
        throw new JsonException("JsonObject must end with '}',", reader);
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
        return this.isJsonArray != null ? this.isJsonArray.toStringBeauty() : this.toString(4, 1, null);
    }

    String toString(int tabSize, int index, char[] lastTab) {
        StringBuilder builder = new StringBuilder();
        char[] tab = JsonObject.createTab(tabSize, index, lastTab);
        builder.append('{');
        if (length > 0)
            builder.append('\n');

        for (int i = 0; i < this.length; ++i) {
            Item item = this.items[i];
            builder.append(tab);
            JsonObject.makeQuote(item.getKey(), builder);
            builder.append(": ");
            JsonObject.appendValue(item.value, i < length - 1, tabSize, index, tab, builder);
        }

        if (lastTab != null && length > 0)
            builder.append(lastTab);
        builder.append("}");

        return builder.toString();
    }
}
