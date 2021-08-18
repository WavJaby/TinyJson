package com.wavjaby.json;

public class NumberParser {
    Object toNumber(String value, boolean isInt, boolean isFloat, int index) {
        if (value.length() != 0)
            //是數字
            if (isInt) {
                //是long
                if (value.length() > 9) {
                    long valLong = Long.parseLong(value);
                    //是long
                    if (valLong > Integer.MAX_VALUE || valLong < Integer.MIN_VALUE) {
                        return valLong;
                    }
                    //是int
                    else {
                        return (int) valLong;
                    }
                }
                //是int
                else {
                    return Integer.parseInt(value);
                }
            }
            //是浮點數
            else if (isFloat) {
                double valDouble = Double.parseDouble(value);
                //是double
                if (valDouble > Integer.MAX_VALUE || valDouble < Integer.MIN_VALUE) {
                    return valDouble;
                } else {
                    return (float) valDouble;
                }
            }
            //是布林值
            else if (value.equalsIgnoreCase("true")) {
                return true;
            } else if (value.equalsIgnoreCase("false")) {
                return false;
            } else if (value.equalsIgnoreCase("null")) {
                return null;
            }
        throw new JsonException("value wrong at index: " + index);
    }

    Object toNumber(String value, boolean isInt) {
        //是布林值
        if (value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equalsIgnoreCase("false")) {
            return false;
        } else if (value.equalsIgnoreCase("null")) {
            return null;
        }
        //是數字
        else if (isInt) {
            //是long
            if (value.length() > 9) {
                long valLong = Long.parseLong(value);
                //是long
                if (valLong > Integer.MAX_VALUE || valLong < Integer.MIN_VALUE) {
                    return valLong;
                }
                //是int
                else {
                    return (int) valLong;
                }
            }
            //是int
            else {
                return Integer.parseInt(value);
            }
        }
        //是浮點數
        else {
            double valDouble = Double.parseDouble(value);
            //是double
            if (valDouble > Integer.MAX_VALUE || valDouble < Integer.MIN_VALUE) {
                return valDouble;
            } else {
                return (float) valDouble;
            }
        }
    }
}
