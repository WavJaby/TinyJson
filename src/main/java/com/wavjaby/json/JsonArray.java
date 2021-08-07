package com.wavjaby.json;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

public class JsonArray extends ArrayValueGetter implements Serializable, Iterable<Object> {
    private final int arraySize = 10;
    private Object[] items = new Object[arraySize];
    public int length = 0;

    int EndPos;

    public JsonArray(String input) {
        if (input.length() == 0)
            return;
        int i = 0;
        while (input.charAt(i) != '{' && input.charAt(i) != '[') {
            i++;
            if (i >= input.length())
                throw new JsonException("JsonArray must start with '['");
        }
        if (input.charAt(i) == '{') {
            throw new JsonException("JsonArray must start with '['");
        }
        i++;

        boolean moreValue = true;
        for (; i < input.length(); i++) {
            char thisChar = input.charAt(i);

            //skip char
            if (thisChar <= ' ') {
                continue;
            }

            //find value start
            if (!moreValue && thisChar == ',') {
                moreValue = true;
                continue;
            }

            //find string
            if (moreValue && thisChar == '"') {
                i++;
                int start = i;
                while (input.charAt(i) != '"' || (input.charAt(i - 1) == '\\' && input.charAt(i) == '"')) {
                    i++;
                    if (i == input.length()) {
                        throw new JsonException("String must end with '\"'");
                    }
                }
                String value = input.substring(start, i);
                add(value);
                moreValue = false;
                continue;
            }

            //is JsonObject
            if (moreValue && thisChar == '{') {
                JsonObject jsonObject = new JsonObject(input.substring(i));
                add(jsonObject);
                i += jsonObject.EndPos;
                moreValue = false;
                continue;
            }

            //is JsonArray
            if (moreValue && thisChar == '[') {
                JsonArray jsonArray = new JsonArray(input.substring(i));
                add(jsonArray);
                i += jsonArray.EndPos;
                moreValue = false;
                continue;
            }

            //is other value
            if (moreValue) {
                int valueEnd = i;
                boolean isInt = true;
                char nextChar;
                while ((nextChar = input.charAt(valueEnd++)) != ',' && nextChar != ']') {
                    //not int
                    if (nextChar == '.' || nextChar == 'e' || nextChar == 'E')
                        isInt = false;
                    if (valueEnd == input.length())
                        throw new JsonException("JsonArray must end with ']'");
                }
                if (valueEnd - i > 1) {
                    String value = input.substring(i, valueEnd - 1);
                    add(toNumber(value, isInt));
                    moreValue = false;
                } else if (length > 0)
                    throw new JsonException("JsonArray[" + length + "] missing value");
            }

            if (thisChar == ']') {
                EndPos = i;
                return;
            }
        }
        throw new JsonException("JsonArray must end with ']'");
    }

    public String getString(int index) {
        return String.valueOf(getObject(index));
    }

    public int getInteger(int index) {
        Object obj = getObject(index);
        if (obj instanceof String)
            return Integer.parseInt((String) obj);
        return (int) obj;
    }

    public long getLong(int index) {
        Object obj = getObject(index);
        if (obj instanceof String)
            return Long.parseLong((String) obj);
        if (obj instanceof Integer)
            return (long) (int) obj;
        return (long) obj;
    }

    public float getFloat(int index) {
        return (float) getObject(index);
    }

    public double getDouble(int index) {
        Object obj = getObject(index);
        if (obj instanceof String)
            return Double.parseDouble((String) obj);
        if (obj instanceof Float)
            return (double) (float) obj;
        return (double) obj;
    }

    public boolean getBoolean(int index) {
        return (boolean) getObject(index);
    }

    public Object getObject(int index) {
        if (index < length)
            return items[index];
        throw new ArrayIndexOutOfBoundsException();
    }

    public JsonObject get(int index) {
        return (JsonObject) getObject(index);
    }

    public JsonArray getJsonArray(int index) {
        return (JsonArray) getObject(index);
    }

    //setter
    public JsonArray add(Object item) {
        if (items.length == length) {
            items = arrayAddLength();
        }
        items[length] = item;
        length++;
        return this;
    }

    public JsonArray addAll(JsonArray jsonArray) {
        for (Object i : jsonArray.toArray()) {
            add(i);
        }
        return this;
    }

    public JsonArray remove(int index) {
        if (index >= length)
            return this;

        index++;
        System.arraycopy(items, index + 1, items, index, length - index + 1);
        length--;
        return this;
    }

    public Object[] toArray() {
        Object[] result = new Object[length];
        System.arraycopy(items, 0, result, 0, length);
        return result;
    }

    /**
     * extend array
     */
    private Object[] arrayAddLength() {
        int newLength = (int) (items.length * 1.5);
        int preserveLength = Math.min(items.length, newLength);
        if (preserveLength > 0) {
            Object[] copy = new Object[newLength];
            System.arraycopy(items, 0, copy, 0, items.length);
            return copy;
        }
        throw new ArrayIndexOutOfBoundsException("negative array size");
    }

    /**
     * find key index
     */

    public boolean content(Object value) {
        return indexOf(value) != -1;
    }

    private int findLoc = 0;

    public int indexOf(Object obj) {
        for (int i = 0; i < length; i++) {
            if (items[findLoc].equals(obj))
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
        return toString(-1);
    }

    public String toStringBeauty() {
        return toString(1);
    }

    public String toString(int index) {
        String tabLen = "";
        StringBuilder builder = new StringBuilder();
        if (index < 0) {
            builder.append("[");
            --index;
        } else {
            byte[] spaceLen = new byte[index * 2];

            for (int i = 0; i < index * 2; ++i) {
                spaceLen[i] = (byte) (spaceLen[i] + 32);
            }

            tabLen = new String(spaceLen);
            if (this.length > 0)
                builder.append("[\n");
            else
                builder.append("[");
        }

        for (int i = 0; i < length; ++i) {
            Object item = items[i];
            if (item == null) {
                builder.append(tabLen).append("null");
            } else if (item.getClass() == JsonObject.class) {
                builder.append(tabLen).append(((JsonObject) item).toString(index + 1));
            } else if (item.getClass() == JsonArray.class) {
                builder.append(tabLen).append(((JsonArray) item).toString(index + 1));
            } else if (item.getClass() == String.class) {
                builder.append(tabLen).append("\"").append(item).append("\"");
            } else {
                builder.append(tabLen).append(item);
            }

            if (i < length - 1) {
                builder.append(",");
            }

            if (index > 0) {
                builder.append("\n");
            }
        }

        if (index > 0) {
            builder.append(tabLen.substring(2));
        }

        builder.append("]");
        return builder.toString();
    }

    /**
     * use in foreach
     */
    @Override
    public Iterator<Object> iterator() {
        return Arrays.asList(toArray()).iterator();
    }
}
