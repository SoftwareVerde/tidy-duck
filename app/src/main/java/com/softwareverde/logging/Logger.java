package com.softwareverde.logging;

public interface Logger {
    enum LogLevel {
        DEBUG, INFO, WARN, ERROR, OFF
    }

    String getName();
    LogLevel getLogLevel();

    void debug(String message);
    void debug(String message, boolean shouldLogStackTrace);
    void debug(String message, Exception exception);

    void info(String message);
    void info(String message, boolean shouldLogStackTrace);
    void info(String message, Exception exception);

    void warn(String message);
    void warn(String message, boolean shouldLogStackTrace);
    void warn(String message, Exception exception);

    void error(String message);
    void error(String message, boolean shouldLogStackTrace);
    void error(String message, Exception exception);
}