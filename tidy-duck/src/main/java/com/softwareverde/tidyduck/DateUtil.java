package com.softwareverde.tidyduck;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil extends com.softwareverde.util.DateUtil {

    /**
     * Returns a java.util.Date object by parsing dateString.
     *  The dateString should be in the yyyy-MM-dd format.
     *  Ex: 2000-01-01
     * Returns null if the parse failed.
     */
    public static Date dateFromDateString(final String dateString) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(dateString);
        } catch (final ParseException e) {
            return null;
        }
    }
}
