package com.wavjaby.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonObjectReader {
    private final char[] input;
    public int i = 0;

    public JsonObjectReader(String input) {
        this.input = input.toCharArray();
    }

    public char nextChar() {
        if (++i == input.length)
            return '\0';
        char next;
        while ((next = input[i]) <= ' ') {
            if (++i == input.length)
                return '\0';
        }
        return next;
    }

    public String readString() {
        if (++i == input.length)
            throw new JsonException("String must start with '\"'");
        int startIndex = i;
        while (input[i] != '"' || input[i - 1] == '\\') {
            if (++i == input.length)
                throw new JsonException("String must start with '\"'");
        }
        return new String(input, startIndex, i - startIndex);
    }

    public Object readValue() {
        boolean isInt = true;
        boolean isFloat = true;
        int startIndex = i;
        while (i + 1 < input.length) {
            if (input[i] <= ' ' || input[i] == ',' || input[i] == ']' || input[i] == '}')
                break;
            if (!(input[i] >= '0' && input[i] <= '9' || input[i] == '-' || input[i] == '+')) {
                if (!(input[i] == '.' || input[i] == 'e' || input[i] == 'E'))
                    isFloat = false;
                isInt = false;
            }
            i++;
        }
        String value = new String(input, startIndex, i-- - startIndex);
        //boolean
        if (value.equalsIgnoreCase("true"))
            return true;
        else if (value.equalsIgnoreCase("false"))
            return false;
        else if (value.equalsIgnoreCase("null")) //null
            return null;
        else
            return toNumber(value, isInt, isFloat, startIndex);
    }

    public void findJsonStart() {
        int startIndex = i;
        if (input.length == 0)
            throw new JsonException("JsonObject must start with '{'");
        while (input[i] != '{' && input[i] != '[') {
            if (++i == input.length)
                throw new JsonException("JsonObject must start with '{', at index: " + startIndex);
        }
    }

    public void findArrayStart() {
        int startIndex = i;
        if (input.length == 0)
            throw new JsonException("JsonArray must start with '['");
        while (input[i] != '{' && input[i] != '[') {
            if (++i == input.length)
                throw new JsonException("JsonArray must start with '[', at index: " + startIndex);
        }
        if (input[i] == '{')
            throw new JsonException("not a JsonArray, JsonArray must start with '['");
    }

    public char thisChar() {
        return input[i];
    }

    // number parser
    static Number toNumber(String value, boolean isInt, boolean isFloat, int index) {
        if (value.length() != 0) {
            // -0
            if (value.length() == 2 && value.charAt(0) == '-' && value.charAt(1) == '0')
                return -0.0D;

            // Digit
            if (isInt) {
                // Can be Long
                if (value.length() > 9) {
                    // Can be BigInteger
                    if (value.length() > 18) {
                        BigInteger valBig = new BigInteger(value, 10);
                        // Long
                        if (valBig.bitLength() <= 63)
                            return valBig.longValue();
                        else // Big
                            return valBig;
                    } else {
                        long valLong = Long.parseLong(value, 10);
                        // Long
                        if (valLong > Integer.MAX_VALUE || valLong < Integer.MIN_VALUE)
                            return valLong;
                        else // Int
                            return (int) valLong;
                    }
                }
                // Int
                else
                    return Integer.valueOf(value, 10);
            }
            // Float
            else if (isFloat) {
                try {
                    return new BigDecimal(value);
                } catch (NumberFormatException retryAsDouble) {
                    // Support hex floats
                    try {
                        return Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        throw new JsonException("Invalid number at index: " + index);
                    }
                }
            }
        }
        throw new JsonException("Invalid number at index: " + index);
    }
}
