package com.softwareverde.logging.slf4j;

import com.softwareverde.logging.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLogger implements Logger {
    protected final String _clazzName;
    protected final org.slf4j.Logger _logger;

    public Slf4jLogger(final Class clazz) {
        _clazzName = clazz.getName();
        _logger = LoggerFactory.getLogger(clazz);
    }

    public Slf4jLogger(final String clazzName) {
        _clazzName = clazzName;
        _logger = LoggerFactory.getLogger(clazzName);
    }

    @Override
    public String getName() {
        return _clazzName;
    }

    @Override
    public LogLevel getLogLevel() {
        if (_logger.isDebugEnabled()) {
            return LogLevel.DEBUG;
        }
        else if (_logger.isInfoEnabled()) {
            return LogLevel.INFO;
        }
        else if (_logger.isWarnEnabled()) {
            return LogLevel.WARN;
        }
        else if (_logger.isErrorEnabled()) {
            return LogLevel.ERROR;
        }

        return LogLevel.OFF;
    }

    @Override
    public void debug(final String message) {
        _logger.debug(message, new Exception());
    }

    @Override
    public void debug(final String message, final boolean shouldLogStackTrace) {
        _logger.debug(message, (shouldLogStackTrace ? new Exception() : null));
    }

    @Override
    public void debug(final String message, final Exception exception) {
        _logger.debug(message, exception);
    }

    @Override
    public void info(final String message) {
        _logger.info(message, new Exception());
    }

    @Override
    public void info(final String message, final boolean shouldLogStackTrace) {
        _logger.info(message, (shouldLogStackTrace ? new Exception() : null));
    }

    @Override
    public void info(final String message, final Exception exception) {
        _logger.info(message, exception);
    }

    @Override
    public void warn(final String message) {
        _logger.warn(message, new Exception());
    }

    @Override
    public void warn(final String message, final boolean shouldLogStackTrace) {
        _logger.warn(message, (shouldLogStackTrace ? new Exception() : null));
    }

    @Override
    public void warn(final String message, final Exception exception) {
        _logger.warn(message, exception);
    }

    @Override
    public void error(final String message) {
        _logger.error(message, new Exception());
    }

    @Override
    public void error(final String message, final boolean shouldLogStackTrace) {
        _logger.error(message, (shouldLogStackTrace ? new Exception() : null));
    }

    @Override
    public void error(final String message, final Exception exception) {
        _logger.error(message, exception);
    }
}
