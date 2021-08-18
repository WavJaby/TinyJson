package com.wavjaby.json;

class JsonObjectReader extends NumberParser {
    private final char[] input;
    public int i = 0;

    JsonObjectReader(String input) {
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
        char[] out = new char[i - startIndex];
        System.arraycopy(input, startIndex, out, 0, i - startIndex);
        return new String(out);
    }

    public Object readValue() {
        boolean isInt = true;
        boolean isFloat = true;
        int startIndex = i;
        while (i + 1 < input.length) {
            if (input[i] <= ' ' || input[i] == ',' || input[i] == ']' || input[i] == '}')
                break;
            if (!isInt()) {
                if (!isFloat())
                    isFloat = false;
                isInt = false;
            }

            i++;
        }
        char[] out = new char[i - startIndex];
        System.arraycopy(input, startIndex, out, 0, i - startIndex);
        i--;
        return toNumber(new String(out), isInt, isFloat, startIndex);
    }

    private boolean isInt() {
        return input[i] >= '0' && input[i] <= '9';
    }

    private boolean isFloat() {
        return input[i] == '.' || input[i] == 'e' || input[i] == 'E';
    }

    public void findJsonStart() {
        int startIndex = i;
        if (input.length == 0)
            throw new JsonException("JsonObject must start with '{'");
        while (input[i] != '{' && input[i] != '[') {
            if (++i == input.length)
                throw new JsonException("JsonArray must start with '{', at index: " + startIndex);
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
}
