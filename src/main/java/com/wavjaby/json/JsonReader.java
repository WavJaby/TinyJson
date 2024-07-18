package com.wavjaby.json;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;

public class JsonReader {
    private final char[] input;
    private int i = 0;

    private int lastI = 0;
    private int lastLineCount = 1;
    private int lastLineOffset = 0;
    private int lineCount = 1;
    private int lineOffset = 0;

    public JsonReader(String input) {
        this.input = input.toCharArray();
    }

    public JsonReader(InputStream input, Charset charset) {
        char[] chars = new char[0];
        try {
            InputStreamReader inputReader = new InputStreamReader(input, charset);
            CharArrayWriter out = new CharArrayWriter();
            char[] buff = new char[1024];
            int len;
            while ((len = inputReader.read(buff, 0, buff.length)) > 0)
                out.write(buff, 0, len);
            chars = out.toCharArray();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.input = chars;
    }

    public char nextChar() {
        lastI = i;
        lastLineCount = lineCount;
        lastLineOffset = lineOffset;
        ++lineOffset;
        if (++i == input.length)
            return '\0';
        char next;
        while ((next = input[i]) <= ' ') {
            ++lineOffset;
            if (next == '\n') {
                ++lineCount;
                lineOffset = 0;
            }
            if (++i == input.length)
                return '\0';
        }
        return next;
    }

    private final StringBuilder stringBuilderCache = new StringBuilder();

    public String readString() {
        lastI = i;
        lastLineOffset = lineOffset;
        char lastChar = '\0';
        if (++i == input.length)
            throw new JsonException("String must end with '\"'", this);
        boolean isEscape;
        final int start = i;
        int lastStart = i;
        stringBuilderCache.setLength(0);
        while (((isEscape = (lastChar == '\\')) || input[i] != '"') && input[i] >= ' ') {
            lastChar = input[i];
            if (isEscape) {
                stringBuilderCache.append(input, lastStart, i - lastStart - 1);
                switch (input[i]) {
                    case 'b':
                        stringBuilderCache.append('\b');
                        break;
                    case 't':
                        stringBuilderCache.append('\t');
                        break;
                    case 'n':
                        stringBuilderCache.append('\n');
                        break;
                    case 'f':
                        stringBuilderCache.append('\f');
                        break;
                    case 'r':
                        stringBuilderCache.append('\r');
                        break;
                    case 'u':
                        try {
                            stringBuilderCache.append((char) Integer.parseInt(new String(input, ++i, 4), 16));
                        } catch (NumberFormatException e) {
                            throw new JsonException("Illegal escape ", this);
                        }
                        i += 3;
                        break;
                    case '\\':
                        lastChar = '\0';
                    case '"':
                    case '\'':
                    case '/':
                        stringBuilderCache.append(input[i]);
                        break;
                    default:
                        throw new JsonException("Illegal escape ", this);
                }
                lastStart = i + 1;
            }
            if (++i == input.length)
                throw new JsonException("String must end with '\"'", this);
        }
        if (input[i] != '"')
            throw new JsonException("String must end with '\"'", this);
        final int len = i - start;
        lineOffset += len + 1;

        if (lastStart == start)
            return new String(input, start, len);
        return stringBuilderCache.append(input, lastStart, i - lastStart).toString();
    }

    public Object readValue() {
        boolean isInt = true;
        boolean isFloat = true;
        lastI = i;
        lastLineOffset = lineOffset;
        while (input[i] > ' ' && input[i] != ',' && input[i] != ']' && input[i] != '}') {
            if (!(input[i] >= '0' && input[i] <= '9' || input[i] == '-' || input[i] == '+')) {
                if (!(input[i] == '.' || input[i] == 'e' || input[i] == 'E'))
                    isFloat = false;
                isInt = false;
            }
            if (++i == input.length)
                throw new JsonException("Value format error", this);
        }
        final int len = i-- - lastI;
        lineOffset += len;
        String value = new String(input, lastI, len);
        //boolean
        if (value.equalsIgnoreCase("true"))
            return true;
        else if (value.equalsIgnoreCase("false"))
            return false;
        else if (value.equalsIgnoreCase("null")) //null
            return null;
        else
            return toNumber(value, isInt, isFloat);
    }

    public void findJsonStart() {
        if (input.length == 0)
            throw new JsonException("JsonObject must start with '{',", this);
        while (input[i] <= ' ') {
            ++lineOffset;
            if (input[i] == '\n') {
                ++lineCount;
                lineOffset = 0;
            }
            if (++i == input.length)
                throw new JsonException("JsonObject must start with '{',", this);
        }
        lastI = i;
        lastLineCount = lineCount;
        lastLineOffset = lineOffset;
    }

    public void findArrayStart() {
        if (input.length == 0)
            throw new JsonException("JsonArray must start with '[',", this);
        while (input[i] <= ' ') {
            ++lineOffset;
            if (input[i] == '\n') {
                ++lineCount;
                lineOffset = 0;
            }
            if (++i == input.length)
                throw new JsonException("JsonArray must start with '[',", this);
        }
        lastI = i;
        lastLineCount = lineCount;
        lastLineOffset = lineOffset;
        if (input[i] != '[')
            throw new JsonException("not a JsonArray, JsonArray must start with '[',", this);
    }

    public char thisChar() {
        return input[i];
    }

    public String createPart() {
        int start;
        for (start = lastI; start > 0 && lastI - start < 50 && input[start - 1] >= ' '; start--) {
        }
        int end;
        for (end = lastI; end < input.length && end - lastI < 50 && input[end] >= ' '; end++) {
        }

        if (start == lastI && start == 0)
            return " at " + lastI + '[' + lastLineCount + ':' + lastLineOffset + "]";
        return " at " + lastI + '[' + lastLineCount + ':' + lastLineOffset + "]\n" +
                new String(input, start, end - start) + '\n' +
                String.format("%" + (lastI - start) + "c^", ' ');
    }

    // Number parser
    private Number toNumber(final String value, final boolean isInt, final boolean isFloat) {
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
                        final BigInteger valBig = new BigInteger(value, 10);
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
                        throw new JsonException("Invalid number", this);
                    }
                }
            }
        }
        throw new JsonException("Invalid number", this);
    }
}
