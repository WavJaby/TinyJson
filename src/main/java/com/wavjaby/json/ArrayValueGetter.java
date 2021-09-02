package com.wavjaby.json;

import java.math.BigInteger;

public abstract class ArrayValueGetter {
    abstract Object getObject(int index);

    @SuppressWarnings("unused")
    public String getString(int index) {
        Object obj = getObject(index);
        if (obj == null)
            return null;
        if (obj instanceof String)
            return (String) obj;
        throw wrongValueFormatException(index, "String");
    }

    @SuppressWarnings("unused")
    public int getInt(int index) {
        Object obj = getObject(index);
        if (obj instanceof Integer)
            return (int) obj;
        if (obj instanceof Number)
            return ((Number) obj).intValue();
        try {
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(index, "int", e);
        }
    }

    @SuppressWarnings("unused")
    public long getLong(int index) {
        Object obj = getObject(index);
        if (obj instanceof Long)
            return (long) obj;
        if (obj instanceof Number)
            return ((Number) obj).longValue();
        try {
            return Long.parseLong(obj.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(index, "long", e);
        }
    }

    @SuppressWarnings("unused")
    public BigInteger getBigInteger(int index) {
        Object obj = getObject(index);
        if (obj instanceof BigInteger)
            return (BigInteger) obj;
        if (obj instanceof Number)
            return BigInteger.valueOf(((Number) obj).longValue());
        try {
            return new BigInteger(obj.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(index, "BigInteger", e);
        }
    }

    @SuppressWarnings("unused")
    public float getFloat(int index) {
        Object obj = getObject(index);
        if (obj instanceof Float)
            return (float) obj;
        if (obj instanceof Number)
            return ((Number) obj).floatValue();
        try {
            return Float.parseFloat(obj.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(index, "float", e);
        }
    }

    @SuppressWarnings("unused")
    public double getDouble(int index) {
        Object obj = getObject(index);
        if (obj instanceof Double)
            return (double) obj;
        if (obj instanceof Number)
            return ((Number) obj).doubleValue();
        try {
            return Double.parseDouble(obj.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(index, "double", e);
        }
    }

    @SuppressWarnings("unused")
    public boolean getBoolean(int index) {
        Object obj = getObject(index);
        if (obj instanceof Boolean)
            return (boolean) obj;
        if (obj instanceof String)
            if (((String) obj).equalsIgnoreCase("true"))
                return true;
            else if (((String) obj).equalsIgnoreCase("false"))
                return false;
        throw wrongValueFormatException(index, "boolean");
    }

    @SuppressWarnings("unused")
    public JsonObject getJson(int index) {
        Object obj = getObject(index);
        if (obj == null)
            return null;
        if (obj instanceof JsonObject)
            return (JsonObject) obj;
        throw wrongValueFormatException(index, "JsonObject");
    }

    @SuppressWarnings("unused")
    public JsonArray getArray(int index) {
        Object obj = getObject(index);
        if (obj == null)
            return null;
        if (obj instanceof JsonArray)
            return (JsonArray) obj;
        throw wrongValueFormatException(index, "JsonArray");
    }

    @SuppressWarnings({"unused", "unchecked"})
    public <T> T get(int index) {
        return (T) getObject(index);
    }

    private static JsonException wrongValueFormatException(int index, String valueType) {
        return new JsonException(
                "JsonArray[" + index + "] is not a " + valueType);
    }

    private static JsonException wrongValueFormatException(int index, String valueType, Throwable e) {
        return new JsonException(
                "JsonArray[" + index + "] is not a " + valueType, e);
    }
}
