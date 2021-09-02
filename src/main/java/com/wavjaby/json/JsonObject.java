package com.wavjaby.json;

import java.io.Serializable;

public class JsonObject extends JsonValueGetter implements Serializable {
    private Item[] items = new Item[10];
    public int length;
    JsonArray isJsonArray = null;

    @SuppressWarnings("unused")
    public JsonObject() {
        length = 0;
    }

    public JsonObject(String input) {
        this(new JsonObjectReader(input));
    }

    public JsonObject(JsonObjectReader reader) {
        length = 0;
        reader.findJsonStart();
        if (reader.thisChar() == '[') {
            isJsonArray = new JsonArray(reader);
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
                        append(new Item(key, new JsonArray(reader)));
                        isValue = false;
                    }
                    break;
                case '{':
                    if (isValue) {
                        append(new Item(key, new JsonObject(reader)));
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
            return items[pos].getValue();
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
    public JsonObject put(String key, Object value) {
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
    public JsonObject addAll(JsonObject jsonObject) {
        if (isJsonArray == null) {
            for (Item i : jsonObject.Items()) {
                put(i.getKey(), i.getValue());
            }
            return this;
        }
        throw new JsonException("This is JsonArray, use \"toJsonArray\" to get it");
    }

    @SuppressWarnings("unused")
    public JsonObject remove(String key) {
        int index;
        if ((index = indexOf(key)) == -1)
            return this;

        System.arraycopy(items, index + 1, items, index, length - index + 1);
        length--;
        findLoc--;
        return this;
    }

    @SuppressWarnings("unused")
    private void append(Item item) {
        if (this.items.length == length) {
            this.items = arrayAddLength();
        }
        this.items[length] = item;
        length++;
    }

    /**
     * extend array
     */
    private Item[] arrayAddLength() {
        int newLength = (int) (items.length * 1.5);
        int preserveLength = Math.min(items.length, newLength);
        if (preserveLength > 0) {
            Item[] copy = new Item[newLength];
            System.arraycopy(items, 0, copy, 0, items.length);
            return copy;
        }
        throw new ArrayIndexOutOfBoundsException("negative array size");
    }

    /**
     * find key index
     */
    private int findLoc = 0;

    public int indexOf(String key) {
        for (int i = 0; i < length; i++) {
            if (items[findLoc].getKey().equals(key))
                return findLoc;

            findLoc++;
            if (findLoc == length)
                findLoc = 0;
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
            builder.append('\"').append(item.getKey()).append('\"').append(':');
            if (item.getValue() == null)
                builder.append("null");
            else if (item.getValue() instanceof String)
                builder.append('\"').append(item.getValue()).append('\"');
            else
                builder.append(item.getValue());

            if (i < length - 1)
                builder.append(",");
        }
        builder.append('}');
        return builder.toString();
    }

    public String toStringBeauty() {
        return this.isJsonArray != null ? this.isJsonArray.toStringBeauty() : this.toString(1, null);
    }

    String toString(int index, char[] lastTab) {
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
            builder.append(tab).append('\"').append(item.getKey()).append(index < 0 ? "\":" : "\": ");
            if (item.getValue() == null)
                builder.append("null");
            else if (item.getValue() instanceof JsonObject)
                builder.append(((JsonObject) item.getValue()).toString(index + 1, tab));
            else if (item.getValue() instanceof JsonArray)
                builder.append(((JsonArray) item.getValue()).toString(index + 1, tab));
            else if (item.getValue() instanceof String)
                builder.append('\"').append(item.getValue()).append('\"');
            else
                builder.append(item.getValue());

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
