package com.softwareverde.tidyduck.util;

import com.softwareverde.util.Util;

import java.io.InputStream;

public class TidyDuckUtil extends Util {

    public static boolean isBlank(String string) {
        return string == null && string.trim().length() == 0;
    }

    public static String getInputStreamAsString(InputStream inputStream) {
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
