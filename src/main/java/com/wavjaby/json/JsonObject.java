package com.wavjaby.json;

import java.io.Serializable;

public class JsonObject extends JsonValueGetter implements Serializable {
    private final int arraySize = 10;
    private Item[] items = new Item[arraySize];
    public int length = 0;
    JsonArray isJsonArray = null;

    int EndPos;

    public JsonObject(String input) {
        if (input.length() == 0)
            return;
        int i = 0;
        while (input.charAt(i) != '{' && input.charAt(i) != '[') {
            i++;
            if (i == input.length()) {
                throw new JsonException("JsonObject must start with '{'");
            }
        }

        if (input.charAt(i) == '[') {
            isJsonArray = new JsonArray(input);
            return;
        }


        boolean isJsonOrArray = false;
        boolean findKey = false;
        String key = null;
        String stringValue = null;

        boolean findValue = false;

        for (; i < input.length(); i++) {
            char thisChar = input.charAt(i);

            //skip char
            if (thisChar <= ' ') {
                continue;
            }

            //find string
            if (thisChar == '"') {
                i++;
                int start = i;
                while (input.charAt(i) != '"' || (input.charAt(i - 1) == '\\' && input.charAt(i) == '"')) {
                    i++;
                    if (i == input.length()) {
                        throw new JsonException("String must end with '\"'");
                    }
                }
                if (!findKey) {
                    key = input.substring(start, i);
                    findKey = true;
                } else if (findValue) {
                    stringValue = input.substring(start, i);
                }
                continue;
            }

            //key end, value start
            if (findKey && thisChar == ':') {
                findValue = true;
                continue;
            }

            //is JsonObject
            if (findValue && thisChar == '{') {
                JsonObject jsonObject = new JsonObject(input.substring(i));
                append(new Item(key, jsonObject));
                i += jsonObject.EndPos;
                isJsonOrArray = true;
                continue;
            }

            //is JsonArray
            if (findValue && thisChar == '[') {
                JsonArray jsonArray = new JsonArray(input.substring(i));
                append(new Item(key, jsonArray));
                i += jsonArray.EndPos;
                isJsonOrArray = true;
                continue;
            }

            //value end
            if (findValue) {
                int valueEnd = i;
                boolean isInt = true;
                char nextChar;
                while ((nextChar = input.charAt(valueEnd++)) != ',' && nextChar != '}') {
                    //not int
                    if (nextChar == '.' || nextChar == 'e' || nextChar == 'E')
                        isInt = false;
                    if (valueEnd == input.length())
                        throw new JsonException("JsonObject must end with ']'");
                }
                //value is string
                if (stringValue != null) {
                    append(new Item(key, stringValue));
                    stringValue = null;
                }
                //value is number
                else if (valueEnd - i > 1) {
                    String value = input.substring(i, valueEnd - 1);
                    append(new Item(key, toNumber(value, isInt)));
                } else if (!isJsonOrArray && length > 0)
                    throw new JsonException("JsonObject[\"" + key + "\"] missing value");
                findKey = false;
                findValue = false;
                isJsonOrArray = false;
            }

            if (thisChar == '}') {
                EndPos = i;
                return;
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
        return null;
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

//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof JsonObject))
//            return false;
//        JsonObject jsonObject = (JsonObject) obj;
//        if(jsonObject.length != length)
//            return false;
//
//        return true;
//    }

    /**
     * to string
     */
    @Override
    public String toString() {
        return this.isJsonArray != null ? this.isJsonArray.toString() : this.toString(-1);
    }

    public String toStringBeauty() {
        return this.isJsonArray != null ? this.isJsonArray.toStringBeauty() : this.toString(1);
    }

    String toString(int index) {
        String tabLen = "";
        StringBuilder builder = new StringBuilder();
        if (index < 0) {
            builder.append("{");
            --index;
        } else {
            byte[] spaceLen = new byte[index * 2];

            for (int i = 0; i < index * 2; ++i) {
                spaceLen[i] = (byte) (spaceLen[i] + 32);
            }

            tabLen = new String(spaceLen);
            if (length > 0)
                builder.append("{\n");
            else
                builder.append("{");
        }

        for (int i = 0; i < this.length; ++i) {
            Item item = this.items[i];
            builder.append(tabLen).append("\"").append(item.getKey()).append("\": ");
            if (item.getValue() == null) {
                builder.append("null");
            } else if (item.getValue().getClass() == JsonObject.class) {
                builder.append(((JsonObject) item.getValue()).toString(index + 1));
            } else if (item.getValue().getClass() == JsonArray.class) {
                builder.append(((JsonArray) item.getValue()).toString(index + 1));
            } else if (item.getValue().getClass() == String.class) {
                builder.append("\"").append(item.getValue()).append("\"");
            } else {
                builder.append(item.getValue());
            }

            if (i < this.length - 1) {
                builder.append(",");
            }

            if (index > 0) {
                builder.append("\n");
            }
        }

        if (index > 0) {
            builder.append(tabLen.substring(2));
        }

        builder.append("}");
        return builder.toString();
    }
}
