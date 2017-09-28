package com.softwareverde.tomcat.servlet;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.api.AuthenticatedJsonRequestHandler;
import com.softwareverde.tidyduck.database.AccountInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tomcat.api.ApiRoute;
import com.softwareverde.tomcat.api.ApiUrlRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.Map;

public class AuthenticatedJsonServlet extends JsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    private final ApiUrlRouter<AuthenticatedJsonRequestHandler> _apiUrlRouter;

    public AuthenticatedJsonServlet() {
        _apiUrlRouter = new ApiUrlRouter<AuthenticatedJsonRequestHandler>(BASE_API_URL, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account account, final Environment environment) throws Exception {
                return _generateErrorJson("Invalid request.");
            }
        });
    }

    protected void _defineEndpoint(final String endpointPattern, final HttpMethod httpMethod, final AuthenticatedJsonRequestHandler route) {
        _apiUrlRouter.defineEndpoint(endpointPattern, httpMethod, route);
    }

    protected Json handleAuthenticatedRequest(final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
        final ApiRoute<AuthenticatedJsonRequestHandler> route = _apiUrlRouter.route(request, httpMethod);
        final AuthenticatedJsonRequestHandler requestHandler = route.getRequestHandler();
        return requestHandler.handleAuthenticatedRequest(route.getParameters(), request, httpMethod, currentAccount, environment);
    }

    @Override
    protected final Json handleRequest(final HttpServletRequest request, final HttpMethod httpMethod, final Environment environment) throws Exception {

        if (! Session.isAuthenticated(request)) {
            return super._generateErrorJson("Not authenticated.");
        }

        final Long accountId = Session.getAccountId(request);
        _logger.debug("Authenticated as " + accountId.toString());

        try (final DatabaseConnection<Connection> databaseConnection = environment.getDatabase().newConnection()) {
            final AccountInflater accountInflater = new AccountInflater(databaseConnection);
            final Account account = accountInflater.inflateAccount(accountId);

            return this.handleAuthenticatedRequest(request, httpMethod, account, environment);
        }
        catch (Exception e) {
            _logger.error("Unable to complete API call.", e);
            return _generateErrorJson(e.getMessage());
        }
    }
}
