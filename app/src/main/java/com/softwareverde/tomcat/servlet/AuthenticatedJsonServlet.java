package com.softwareverde.tomcat.servlet;

import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AuthenticatedJsonServlet extends JsonServlet {
    public static final String COOKIE_SESSION_NAME = "TDYSESSID";
    private static final Map<String, Long> _sessions = new ConcurrentHashMap<String, Long>();

    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    protected abstract Json handleAuthenticatedRequest(final HttpServletRequest request, final HttpMethod httpMethod, final long accountId, final Environment environment) throws Exception;

    public static Boolean hasSessionCookie(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) { return false; }

        for (final Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(COOKIE_SESSION_NAME)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean isAuthenticated(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) { return false; }

        for (final Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(COOKIE_SESSION_NAME)) {
                return ( Util.coalesce(_sessions.get(cookie.getValue())) > 0 );
            }
        }
        return false;
    }

    public static Long getAccountId(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) { return null; }

        for (final Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(COOKIE_SESSION_NAME)) {
                return _sessions.get(cookie.getValue());
            }
        }
        return null;
    }

    public static void setAccountId(final Long accountId, final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) { return; }

        for (final Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(COOKIE_SESSION_NAME)) {
                _sessions.put(cookie.getValue(), accountId);
            }
        }
    }

    @Override
    protected final Json handleRequest(final HttpServletRequest request, final HttpMethod httpMethod, final Environment environment) throws Exception {
        if (! isAuthenticated(request)) {
            return super._generateErrorJson("Not authenticated.");
        }

        final Long accountId = getAccountId(request);
        return this.handleAuthenticatedRequest(request, httpMethod, accountId, environment);
    }
}
