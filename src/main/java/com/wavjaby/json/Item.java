package com.wavjaby.json;

import java.io.Serializable;

public class Item implements Serializable {
    private String key;
    private Object value;

    Item(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return key + ": " + value;
    }
}
