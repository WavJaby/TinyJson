package com.wavjaby.json.list;

import java.io.Serializable;

public class Item implements Serializable {
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
