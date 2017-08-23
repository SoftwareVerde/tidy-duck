package com.softwareverde.tomcat.servlet;

import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.api.AuthenticatedJsonRoute;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tomcat.api.ApiRoute;
import com.softwareverde.tomcat.api.ApiUrlRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class AuthenticatedJsonServlet extends JsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    private final ApiUrlRouter<AuthenticatedJsonRoute> _apiUrlRouter;

    public AuthenticatedJsonServlet() {
        _apiUrlRouter = new ApiUrlRouter<AuthenticatedJsonRoute>(BASE_API_URL, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                return _generateErrorJson("Invalid request.");
            }
        });
    }

    protected void defineEndpoint(final String endpointPattern, final HttpMethod httpMethod, final AuthenticatedJsonRoute route) {
        _apiUrlRouter.defineEndpoint(endpointPattern, httpMethod, route);
    }

    protected Json handleAuthenticatedRequest(final HttpServletRequest request, final HttpMethod httpMethod, final long accountId, final Environment environment) throws Exception {
        final ApiRoute<AuthenticatedJsonRoute> route = _apiUrlRouter.route(request, httpMethod);
        final AuthenticatedJsonRoute jsonRoute = route.getRoute();
        return jsonRoute.handleAuthenticatedRequest(route.getParameters(), request, httpMethod, accountId, environment);
    }

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
