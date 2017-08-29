package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.database.AccountInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import com.softwareverde.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.Map;

public class AccountManagementServlet extends AuthenticatedJsonServlet {
    private Logger _logger = LoggerFactory.getLogger(getClass());
    
    public AccountManagementServlet() {
        super.defineEndpoint("account/<accountId>", HttpMethod.GET, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final Long providedAccountId = Util.parseLong(parameters.get("accountId"));
                if (providedAccountId < 1) {
                    return _generateErrorJson("Invalid account ID provided.");
                }
                return _getAccount(providedAccountId, environment.getDatabase());
            }
        });
    }

    protected Json _getAccount(final Long accountId, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final AccountInflater accountInflater = new AccountInflater(databaseConnection);
            final Account account = accountInflater.inflateAccount(accountId);

            final Json response = _toJson(account);

            _setJsonSuccessFields(response);
            return response;

        } catch (DatabaseException e) {
            _logger.error("Unable to get account.", e);
            return _generateErrorJson("Unable to get account.");
        }
    }

    protected Json _toJson(final Account account) {
        final Json json = new Json(false);

        json.put("id", account.getId());
        json.put("name", account.getName());
        json.put("username", account.getUsername());
        json.put("companyId", account.getCompany().getId());
        json.put("companyName", account.getCompany().getName());

        return json;
    }
}
