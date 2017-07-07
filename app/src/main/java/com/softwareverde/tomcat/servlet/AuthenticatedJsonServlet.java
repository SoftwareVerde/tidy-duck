package com.softwareverde.tomcat.servlet;

import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public abstract class AuthenticatedJsonServlet extends JsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    protected abstract Json handleAuthenticatedRequest(final HttpServletRequest request, final HttpMethod httpMethod, final long accountId, final Environment environment) throws Exception;

    @Override
    protected final Json handleRequest(final HttpServletRequest request, final HttpMethod httpMethod, final Environment environment) throws Exception {

        if (! Session.isAuthenticated(request)) {
            return super._generateErrorJson("Not authenticated.");
        }

        final Long accountId = Session.getAccountId(request);
        _logger.debug("Authenticated as " + accountId.toString());

        return this.handleAuthenticatedRequest(request, httpMethod, accountId, environment);
    }
}
