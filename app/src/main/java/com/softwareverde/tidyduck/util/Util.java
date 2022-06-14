package com.softwareverde.tidyduck.util;

import com.softwareverde.http.server.servlet.request.Request;

public class Util extends com.softwareverde.util.Util {
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
    public static String getNthFromLastUrlSegment(final Request request, final int index) {
        final String path = request.getFilePath();
        String[] segments = path.split("/");
        return segments[segments.length-1-index];
    }
}
