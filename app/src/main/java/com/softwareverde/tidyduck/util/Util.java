package com.softwareverde.tidyduck.util;

public class Util extends com.softwareverde.util.Util {

    public static Boolean isBlank(final String string) {
        return ( (string == null) || (string.trim().isEmpty()) );
    }
}
