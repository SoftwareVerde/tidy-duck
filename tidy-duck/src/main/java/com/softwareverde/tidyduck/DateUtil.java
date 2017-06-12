package com.softwareverde.tidyduck;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil extends com.softwareverde.util.DateUtil {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Returns a java.util.Date object by parsing dateString.
     *  The dateString should be in the yyyy-MM-dd format.
     *  Ex: 2000-01-01
     * Returns null if the parse failed.
     */
    public static Date dateFromDateString(final String dateString) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dateFormat.parse(dateString);
        } catch (final ParseException e) {
            // TODO: determine how to provide error message to caller.
            return null;
        }
    }

    public static String dateToDateString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return dateFormat.format(date);
    }

    public static String timestampToDateString(Long timestamp) {
        if (timestamp == null) {
            return null;
        } else {
            Date date = new Date(timestamp.longValue());
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            return dateFormat.format(date);
        }
    }
}
