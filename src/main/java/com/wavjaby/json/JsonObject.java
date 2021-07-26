package com.wavjaby.json;

import java.io.Serializable;

public class JsonObject implements Serializable {
    private final int arraySize = 10;
    private Item[] items = new Item[arraySize];
    public int length = 0;
    public JsonArray isJsonArray = null;

    public int EndPos;

    public JsonObject() {
    }

    public JsonObject(String input) {
        if (input.length() == 0)
            return;
        int i = 0;
        while (input.charAt(i) != '{' && input.charAt(i) != '[') {
            i++;
            if (i == input.length())
                return;
        }

        if (input.charAt(i) == '[') {
            isJsonArray = new JsonArray(input);
            return;
        }

        boolean inValue = false;
        int stringStart = -1;
        int stringEnd = -1;
        boolean findKey = false;
        String key = null;

        int valueStart = -1;
        int valueEnd = -1;
        boolean findValue = false;

        boolean isInt = true;
        for (; i < input.length(); i++) {
            char thisChar = input.charAt(i);

            //跳過沒在"裡的空白
            if ((!inValue || findValue) && (thisChar == ' ' || thisChar == '\r' || thisChar == '\n')) {
                continue;
            }

            //不是int
            if (findValue && thisChar == '.') {
                isInt = false;
            }

            //找到"
            if (thisChar == '"' && input.charAt(i - 1) != '\\') {
                if (inValue) {
                    inValue = false;
                    if (!findValue)
                        findKey = true;
                    stringEnd = i;
                } else {
                    inValue = true;
                    stringStart = i + 1;
                }
            }

            //值的開始
            if (!inValue && findKey && thisChar == ':') {
                key = input.substring(stringStart, stringEnd);
                //去掉空白
                while (input.charAt(i + 1) == ' ') {
                    i++;
                    if (i == input.length())
                        return;
                }
                valueStart = i + 1;
                isInt = true;
                findValue = true;
                findKey = false;
                stringStart = -1;
            }

            //值是json
            if (!inValue && findValue && thisChar == '{') {
                JsonObject jsonObject = new JsonObject(input.substring(i));
                append(new Item(key, jsonObject));
                i += jsonObject.EndPos;
                findValue = false;
            }

            if (!inValue && findValue && thisChar == '[') {
                JsonArray jsonArray = new JsonArray(input.substring(i));
                append(new Item(key, jsonArray));
                i += jsonArray.EndPos;
                findValue = false;
            }

            //值的結束
            if (!inValue && !findKey && findValue && (thisChar == ',' || thisChar == '}')) {
                //值不是string
                if (stringStart == -1) {
                    String value = input.substring(valueStart, valueEnd);

                    //是布林值
                    if (value.equalsIgnoreCase("true")) {
                        append(new Item(key, true));
                    } else if (value.equalsIgnoreCase("false")) {
                        append(new Item(key, false));
                    } else if (value.equalsIgnoreCase("null")) {
                        this.append(new Item(key, null));
                    }
                    //是數字
                    else if (isInt) {
                        //是long
                        if (value.length() > 9) {
                            long valLong = Long.parseLong(value);
                            //是long
                            if (valLong > Integer.MAX_VALUE || valLong < Integer.MIN_VALUE) {
                                append(new Item(key, valLong));
                            }
                            //是int
                            else {
                                append(new Item(key, (int) valLong));
                            }
                        }
                        //是int
                        else {
                            int valInt = Integer.parseInt(value);
                            append(new Item(key, valInt));
                        }
                    }
                    //是浮點數
                    else {
                        double valDouble = Double.parseDouble(value);
                        //是double
                        if (valDouble > Integer.MAX_VALUE || valDouble < Integer.MIN_VALUE) {
                            append(new Item(key, valDouble));
                        } else {
                            append(new Item(key, (float) valDouble));
                        }
                    }

                } else {
                    String value = input.substring(stringStart, stringEnd);
                    append(new Item(key, value));
                }
                findValue = false;

            }

            //到底了
            if (!inValue && thisChar == '}') {
                break;
            }

            //找值的end
            if (findValue) {
                valueEnd = i + 1;
            }
        }
        EndPos = i;
    }

    public boolean containsKey(String key) {
        return indexOf(key) > -1;
    }

    public boolean notNull(String key) {
        return indexOf(key) > -1 && getObject(key) != null;
    }

    //getter
    public Item getItem(String key) {
        int pos = indexOf(key);
        if (pos > -1)
            return items[pos];
        else
            return null;
    }

    public Object getObject(String key) {
        int pos = indexOf(key);
        if (pos > -1)
            return items[pos].getValue();
        else
            return null;
    }

    public String getString(String key) {
        Object obj = getObject(key);
        if (obj == null)
            return null;
        return (String) obj;
    }

    public int getInteger(String key) {
        Object obj = getObject(key);
        if (obj instanceof String)
            return Integer.parseInt((String) obj);
        return (int) obj;
    }

    public long getLong(String key) {
        Object obj = getObject(key);
        if (obj instanceof String)
            return Long.parseLong((String) obj);
        if (!(obj instanceof Long))
            return (long) (int) obj;
        return (long) obj;
    }

    public float getFloat(String key) {
        Object obj = getObject(key);
        return (float) obj;
    }

    public double getDouble(String key) {
        Object obj = getObject(key);
        if (obj instanceof String)
            return Double.parseDouble((String) obj);
        if (obj instanceof Float)
            return (double) (float) obj;
        return (double) obj;
    }

    public boolean getBoolean(String key) {
        return (boolean) getObject(key);
    }

    public JsonObject get(String key) {
        return (JsonObject) getObject(key);
    }

    public JsonArray getJsonArray(String key) {
        return (JsonArray) getObject(key);
    }

    public JsonObject get(int index) {
        if (isJsonArray != null)
            return isJsonArray.get(index);
        return null;
    }

    public Item[] Items() {
        Item[] out = new Item[length];
        System.arraycopy(items, 0, out, 0, length);
        return out;
    }

    //setter
    public JsonArray add(Object item) {
        if (isJsonArray != null) {
            isJsonArray.add(item);
            return isJsonArray;
        } else
            return null;
    }

    public JsonArray remove(int index) {
        if (isJsonArray != null) {
            isJsonArray.remove(index);
            return isJsonArray;
        } else
            return null;
    }

    public JsonObject addAll(JsonObject jsonObject) {
        if (isJsonArray == null) {
            for (Item i : jsonObject.Items()) {
                put(i.getKey(), i.getValue());
            }
            return this;
        }
        return null;
    }

    public JsonObject put(String key, Object value) {
        int pos = indexOf(key);
        if (pos > -1)
            items[pos].setValue(value);
        else
            append(new Item(key, value));
        return this;
    }

    private void append(Item item) {
        if (this.items.length == length) {
            this.items = arrayAddLength();
        }
        this.items[length] = item;
        length++;
    }

    public JsonObject remove(String key) {
        int index;
        if ((index = indexOf(key)) == -1)
            return this;

        System.arraycopy(items, index + 1, items, index, length - index + 1);
        length--;
        findLoc--;
        return this;
    }

    /**
     * extend array
     */
//    public static <T> T[] arrayAddLength(T[] original) {
//        int newLength = (int) (original.length * 1.5);
//        int preserveLength = Math.min(original.length, newLength);
//        if (preserveLength > 0) {
//            @SuppressWarnings("unchecked")
//            T[] copy = (T[]) Array.newInstance(original.getClass().getComponentType(), newLength);
//            System.arraycopy(original, 0, copy, 0,
//                    Math.min(original.length, newLength));
//            return copy;
//        }
//        throw new ArrayIndexOutOfBoundsException("negative array size");
//    }
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
            builder.append("{\n");
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
