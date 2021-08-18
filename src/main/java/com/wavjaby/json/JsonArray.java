package com.wavjaby.json;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

public class JsonArray extends ArrayValueGetter implements Serializable, Iterable<Object> {
    private final int arraySize = 10;
    private Object[] items = new Object[arraySize];
    public int length = 0;

    public JsonArray(String input) {
        this(new JsonObjectReader(input));
    }

    public JsonArray(JsonObjectReader reader) {
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
                    add(new JsonArray(reader));
                    break;
                case '{':
                    add(new JsonObject(reader));
                    break;
                default:
                    add(reader.readValue());
            }
        }
        throw new JsonException("JsonArray must end with ']'");
    }

    @Override
    public Object getObject(int index) {
        if (index < length)
            return items[index];
        throw new ArrayIndexOutOfBoundsException();
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

    public String toStringBeauty() {
        return toString(1, null);
    }

    String toString(int index, char[] lastTab) {
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

//    public String toString(int index) {
//        String tabLen = "";
//        StringBuilder builder = new StringBuilder();
//        if (index < 0) {
//            builder.append("[");
//            --index;
//        } else {
//            byte[] spaceLen = new byte[index * 2];
//
//            for (int i = 0; i < index * 2; ++i) {
//                spaceLen[i] = (byte) (spaceLen[i] + 32);
//            }
//
//            tabLen = new String(spaceLen);
//            if (this.length > 0) {
//                builder.append("[\n");
//            } else
//                builder.append("[");
//        }
//
//        for (int i = 0; i < length; ++i) {
//            Object item = items[i];
//            if (item == null) {
//                builder.append(tabLen).append("null");
//            } else if (item instanceof NewJsonObject) {
//                builder.append(tabLen).append(((NewJsonObject) item).toString(index + 1));
//            } else if (item instanceof JsonArray) {
//                builder.append(tabLen).append(((JsonArray) item).toString(index + 1));
//            } else if (item instanceof String) {
//                builder.append(tabLen).append("\"").append(item).append("\"");
//            } else {
//                builder.append(tabLen).append(item);
//            }
//
//            if (i < length - 1)
//                builder.append(",");
//            //beautiful
//            if (index > 0)
//                builder.append("\n");
//        }
//
//        if (index > 0) {
//            builder.append(tabLen.substring(2));
//        }
//
//        builder.append("]");
//        return builder.toString();
//    }

    /**
     * use in foreach
     */
    @Override
    public Iterator<Object> iterator() {
        return Arrays.asList(toArray()).iterator();
    }
}

