package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Settings;
import com.softwareverde.tidyduck.database.AccountInflater;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.Company;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import com.softwareverde.security.SecureHashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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

        super.defineEndpoint("account/<accountId>/change-password", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final Long providedAccountId = Util.parseLong(parameters.get("accountId"));
                if (providedAccountId < 1) {
                    return _generateErrorJson("Invalid account ID provided.");
                }
                return _changePassword(providedAccountId, request, environment.getDatabase());
            }
        });
    }

    protected Json _getAccount(final Long accountId, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final AccountInflater accountInflater = new AccountInflater(databaseConnection);
            final Account account = accountInflater.inflateAccount(accountId);

            final Json response = new Json(false);

            response.put("account", _toJson(account));

            _setJsonSuccessFields(response);
            return response;

        } catch (DatabaseException e) {
            _logger.error("Unable to get account.", e);
            return _generateErrorJson("Unable to get account.");
        }
    }

    protected Json _changePassword(final Long accountId, final HttpServletRequest request, final Database<Connection> database) throws IOException {
        final Json jsonRequest = _getRequestDataAsJson(request);
        final Json response = _generateSuccessJson();
        final String oldPassword = jsonRequest.getString("oldPassword");
        final String newPassword = jsonRequest.getString("newPassword");

        try {
            // TODO: validate new password strength, ie password length, includes one uppercase char, etc.
            if (Util.isBlank(oldPassword)) {
                _logger.error("Unable to change password. Old password is invalid.");
                return _generateErrorJson("Unable to change password. Old password is invalid.");
            }
            if (Util.isBlank(newPassword)) {
                _logger.error("Unable to change password. New password is invalid.");
                return _generateErrorJson("Unable to change password. New password is invalid.");
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            final String newPasswordHash = SecureHashUtil.hashWithPbkdf2(newPassword);
            databaseManager.changePassword(accountId, oldPassword, newPasswordHash);
        }
        catch (final Exception e) {
            _logger.error("Unable to attempt password change.", e);
            return _generateErrorJson("Unable to attempt password change: " + e.getMessage());
        }

        return response;
    }

    protected Json _toJson(final Account account) {
        final Json json = new Json(false);

        json.put("id", account.getId());
        json.put("name", account.getName());
        json.put("username", account.getUsername());
        json.put("company", _toJson(account.getCompany()));
        json.put("settings", _toJson(account.getSettings()));

        return json;
    }

    protected Json _toJson(final Company company) {
        final Json json = new Json(false);

        json.put("id", company.getId());
        json.put("name", company.getName());

        return json;
    }

    protected Json _toJson(final Settings settings) {
        final Json json = new Json(false);

        json.put("theme", settings.getTheme());

        return json;
    }
}
