package com.wavjaby.json;

import java.math.BigInteger;

public class NumberParser {
    private final BigInteger bigLongMax = new BigInteger(String.valueOf(Long.MAX_VALUE));
    private final BigInteger bigLongMin = new BigInteger(String.valueOf(Long.MIN_VALUE));

    Object toNumber(String value, boolean isInt, boolean isFloat, int index) {
        if (value.length() != 0)
            //digit
            if (isInt) {
                //long
                if (value.length() > 9) {
                    //biginteger
                    if (value.length() > 17) {
                        BigInteger valBig = new BigInteger(value);
                        //big
                        if (valBig.compareTo(bigLongMax) > 0 || valBig.compareTo(bigLongMin) < 0)
                            return valBig;
                        else //long
                            return valBig.longValueExact();
                    } else {
                        long valLong = Long.parseLong(value);
                        //long
                        if (valLong > Integer.MAX_VALUE || valLong < Integer.MIN_VALUE)
                            return valLong;
                        else //int
                            return (int) valLong;
                    }
                }
                //int
                else
                    return Integer.parseInt(value);
            }
            //float
            else if (isFloat) {
                double valDouble = Double.parseDouble(value);
                //double
                if (valDouble > Integer.MAX_VALUE || valDouble < Integer.MIN_VALUE)
                    return valDouble;
                else
                    return (float) valDouble;
            }
            //boolean
            else if (value.equalsIgnoreCase("true"))
                return true;
            else if (value.equalsIgnoreCase("false"))
                return false;
            else if (value.equalsIgnoreCase("null")) //null
                return null;

        throw new JsonException("value wrong at index: " + index);
    }
}
