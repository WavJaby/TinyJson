package com.wavjaby.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class ValueGetter<T, K> {

    abstract public Object getObject(K key);

    @SuppressWarnings("unused")
    public String getString(K key) {
        Object obj = getObject(key);
        if (obj == null)
            return null;
        if (obj instanceof String)
            return (String) obj;
        throw wrongValueFormatException(key, "String");
    }

    @SuppressWarnings("unused")
    public int getInt(K key) {
        Object obj = getObject(key);
        if (obj instanceof Integer)
            return (int) obj;
        if (obj instanceof Number)
            return ((Number) obj).intValue();
        try {
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(key, "int", e);
        }
    }

    @SuppressWarnings("unused")
    public long getLong(K key) {
        Object obj = getObject(key);
        if (obj instanceof Long)
            return (long) obj;
        if (obj instanceof Number)
            return ((Number) obj).longValue();
        try {
            return Long.parseLong(obj.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(key, "long", e);
        }
    }

    @SuppressWarnings("unused")
    public BigInteger getBigInteger(K key) {
        Object obj = getObject(key);
        if (obj instanceof BigInteger)
            return (BigInteger) obj;
        if (obj instanceof Number)
            return BigInteger.valueOf(((Number) obj).longValue());
        try {
            return new BigInteger(obj.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(key, "BigInteger", e);
        }
    }


    @SuppressWarnings("unused")
    public float getFloat(K key) {
        Object obj = getObject(key);
        if (obj instanceof Float)
            return (float) obj;
        if (obj instanceof Number)
            return ((Number) obj).floatValue();
        try {
            return Float.parseFloat(obj.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(key, "float", e);
        }
    }

    @SuppressWarnings("unused")
    public double getDouble(K key) {
        Object obj = getObject(key);
        if (obj instanceof Double)
            return (double) obj;
        if (obj instanceof Number)
            return ((Number) obj).doubleValue();
        try {
            return Double.parseDouble(obj.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(key, "double", e);
        }
    }

    @SuppressWarnings("unused")
    public BigDecimal getBigDecimal(K key) {
        Object obj = getObject(key);
        if (obj instanceof BigDecimal)
            return (BigDecimal) obj;
        if (obj instanceof Number)
            return BigDecimal.valueOf(((Number) obj).doubleValue());
        try {
            return new BigDecimal(obj.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(key, "BigDecimal", e);
        }
    }


    @SuppressWarnings("unused")
    public boolean getBoolean(K key) {
        Object obj = getObject(key);
        if (obj instanceof Boolean)
            return (boolean) obj;
        if (obj instanceof String)
            if (((String) obj).equalsIgnoreCase("true"))
                return true;
            else if (((String) obj).equalsIgnoreCase("false"))
                return false;
        throw wrongValueFormatException(key, "boolean");
    }

    @SuppressWarnings("unused")
    public JsonObject getJson(K key) {
        Object obj = getObject(key);
        if (obj == null)
            return null;
        if (obj instanceof JsonObject)
            return (JsonObject) obj;
        throw wrongValueFormatException(key, "JsonObject");
    }

    @SuppressWarnings("unused")
    public JsonArray getArray(K key) {
        Object obj = getObject(key);
        if (obj == null)
            return null;
        if (obj instanceof JsonArray)
            return (JsonArray) obj;
        throw wrongValueFormatException(key, "JsonArray");
    }

    @SuppressWarnings({"unused", "unchecked"})
    public T get(K key) {
        return (T) getObject(key);
    }

    private static JsonException wrongValueFormatException(Object key, String valueType) {
        return new JsonException("Object[\"" + key + "\"] is not a " + valueType);
    }

    private static JsonException wrongValueFormatException(Object key, String valueType, Throwable e) {
        return new JsonException("Object[\"" + key + "\"] is not a " + valueType, e);
    }
}
