package com.softwareverde.tidyduck.util;

public class Util extends com.softwareverde.util.Util {

    public static Boolean isBlank(final String string) {
        return ( (string == null) || (string.trim().isEmpty()) );
    }

    public static String join(final String delimiter, final String[] array) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<array.length; i++) {
            final String arrayElement = array[i];
            stringBuilder.append(arrayElement);
            if (i+1 != array.length) {
                stringBuilder.append(delimiter);
            }
        }
        return stringBuilder.toString();
    }
}
