package com.softwareverde.tomcat.servlet;

import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public abstract class AuthenticatedJsonServlet extends JsonServlet {
    public static final String SESSION_ACCOUNT_ID_KEY = "account_id";

    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    protected abstract Json handleAuthenticatedRequest(final HttpServletRequest request, final HttpMethod httpMethod, final long accountId, final Environment environment) throws Exception;

    public static Boolean isAuthenticated(final HttpServletRequest request) {
        final HttpSession session = request.getSession();
        final Object accountObject = session.getAttribute(SESSION_ACCOUNT_ID_KEY);
        return (accountObject != null);
    }

    public static Long getAccountId(final HttpServletRequest request) {
        final HttpSession session = request.getSession();
        final Object accountObject = session.getAttribute(SESSION_ACCOUNT_ID_KEY);
        return (Long) accountObject;
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
