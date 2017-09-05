package com.softwareverde.tidyduck;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil extends com.softwareverde.util.DateUtil {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Returns a java.util.Date object by parsing dateString.
     *  The dateString should be in the yyyy-MM-dd format.
     *  Ex: 2000-01-01
     * Returns null if the parse failed.
     */
    public static Date dateFromDateString(final String dateString) {
        return convertDateToString(dateString, DATE_FORMAT);
    }

    /**
     * Returns a java.util.Date object by parsing dateTimeString.
     *  The dateTimeString should be the format: yyyy-MM-dd HH:mm:ss
     *  Ex: 2000-01-01 00:00:00
     * Returns null if the parse failed.
     */
    public static Date dateFromDateTimeString(final String dateTimeString) {
        return convertDateToString(dateTimeString, DATE_TIME_FORMAT);
    }

    private static Date convertDateToString(final String value, final String format) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.parse(value);
        } catch (final ParseException e) {
            return null;
        }
    }

    public static String dateToDateString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return dateFormat.format(date);
    }

    public static String dateToDateTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US);
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
