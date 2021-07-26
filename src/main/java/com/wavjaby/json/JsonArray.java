package com.wavjaby.json;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

public class JsonArray implements Serializable, Iterable<Object> {
    private final int arraySize = 1024;
    private Object[] items = new Object[arraySize];
    public int length = 0;

    public int EndPos;

    public JsonArray() {
    }

    public JsonArray(String input) {
        if (input.length() == 0)
            return;
        int i = 0;
        while (input.charAt(i) != '{' && input.charAt(i) != '[') {
            i++;
            if (i >= input.length())
                return;
        }
        if (input.charAt(i) == '{') {
            try {
                throw new ClassCastException("input data is JsonObject, not JsonArray");
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
            return;
        }
        i++;

        boolean inValue = false;
        int stringStart = -1;
        int stringEnd = -1;

        int valueStart = -1;
        int valueEnd = -1;

        boolean isInt = true;
        boolean jsonValue = false;
        for (; i < input.length(); i++) {
            char thisChar = input.charAt(i);

            //跳過沒在"裡的空白
            if (!inValue && (thisChar == ' ' || thisChar == '\r' || thisChar == '\n')) {
                continue;
            }

            //不是int
            if (!inValue && thisChar == '.') {
                isInt = false;
            }

            //找到"
            if (thisChar == '"' && input.charAt(i - 1) != '\\') {
                if (inValue) {
                    inValue = false;
                    stringEnd = i;
                } else {
                    inValue = true;
                    stringStart = i + 1;
                }
            }

            //值的結束
            if (!inValue && (thisChar == ',' || thisChar == ']')) {
                //不是上個value不是json或空陣列
                if (!jsonValue && (valueStart != -1 && valueEnd != -1))
                    //值不是string
                    if (stringStart == -1) {
                        String value = input.substring(valueStart, valueEnd);

                        //是布林值
                        if (value.equalsIgnoreCase("true")) {
                            add(true);
                        } else if (value.equalsIgnoreCase("false")) {
                            add(false);
                        } else if (value.equalsIgnoreCase("null")) {
                            add((Object) null);
                        }
                        //是數字
                        else if (isInt) {
                            //是long
                            if (value.length() > 9) {
                                long valLong = Long.parseLong(value);
                                //是long
                                if (valLong > Integer.MAX_VALUE || valLong < Integer.MIN_VALUE) {
                                    add(valLong);
                                }
                                //是int
                                else {
                                    add((int) valLong);
                                }
                            }
                            //是int
                            else {
                                int valInt = Integer.parseInt(value);
                                add(valInt);
                            }
                        }
                        //是浮點數
                        else {
                            double valDouble = Double.parseDouble(value);
                            //是double
                            if (valDouble > Integer.MAX_VALUE || valDouble < Integer.MIN_VALUE) {
                                add(valDouble);
                            } else {
                                add((float) valDouble);
                            }
                        }
                    } else {
                        String value = input.substring(stringStart, stringEnd);
                        add(value);
                    }

                jsonValue = false;
                stringStart = -1;
                valueStart = -1;
            }

            //值是json
            if (!inValue && thisChar == '{') {
                JsonObject jsonObject = new JsonObject(input.substring(i));
                add(jsonObject);
                i += jsonObject.EndPos;
                jsonValue = true;
            }

            //值是array
            if (!inValue && thisChar == '[') {
                JsonArray jsonArray = new JsonArray(input.substring(i));
                add(jsonArray);
                i += jsonArray.EndPos;
                jsonValue = true;
            }

            //到底了
            if (!inValue && thisChar == ']') {
                break;
            }

            //找值的開頭與結束
            if (!inValue) {
                if (valueStart == -1 && thisChar != ',' && thisChar != '[')
                    valueStart = i;
                valueEnd = i + 1;
            }
        }
        EndPos = i;
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
        if (!(obj instanceof Long))
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
        return (double) obj;
    }

    public boolean getBoolean(int index) {
        return (boolean) getObject(index);
    }

    public Object getObject(int index) {
        return index >= length ? null : items[index];
    }

    public JsonObject get(int index) {
        return (JsonObject) getObject(index);
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
    private Item[] arrayAddLength() {
        int newLength = (int) (items.length * 1.5);
        int preserveLength = Math.min(items.length, newLength);
        if (preserveLength > 0) {
            Item[] copy = new Item[newLength];
            System.arraycopy(items, 0, copy, 0,
                    Math.min(items.length, newLength));
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
            builder.append("[\n");
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
