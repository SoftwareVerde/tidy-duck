package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.*;
import com.softwareverde.tidyduck.database.AccountInflater;
import com.softwareverde.tidyduck.database.CompanyInflater;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.Company;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AccountManagementServlet extends AuthenticatedJsonServlet {
    private Logger _logger = LoggerFactory.getLogger(getClass());

    public AccountManagementServlet() {
        super.defineEndpoint("accounts", HttpMethod.GET, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.ADMIN_MODIFY_USERS);

                return _getAccounts(environment.getDatabase());
            }
        });

        super.defineEndpoint("accounts", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.ADMIN_CREATE_USERS);

                return _insertAccount(request, environment.getDatabase());
            }
        });

        super.defineEndpoint("accounts/<accountId>", HttpMethod.GET, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                final Long providedAccountId = Util.parseLong(parameters.get("accountId"));
                if (providedAccountId < 1) {
                    return _generateErrorJson("Invalid account ID provided.");
                }

                if (!currentAccount.getId().equals(providedAccountId)) {
                    currentAccount.requirePermission(Permission.ADMIN_MODIFY_USERS);
                }

                return _getAccount(currentAccount, providedAccountId, environment.getDatabase());
            }
        });

        super.defineEndpoint("accounts/<accountId>/change-password", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                final Long providedAccountId = Util.parseLong(parameters.get("accountId"));
                if (providedAccountId < 1) {
                    return _generateErrorJson("Invalid account ID provided.");
                }

                if (!currentAccount.getId().equals(providedAccountId)) {
                    currentAccount.requirePermission(Permission.ADMIN_MODIFY_USERS);
                }

                return _changePassword(providedAccountId, request, environment.getDatabase());
            }
        });
    }

    private Json _getAccounts(final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final AccountInflater accountInflater = new AccountInflater(databaseConnection);
            final List<Account> accounts = accountInflater.inflateAccounts();

            final Json response = new Json(false);

            final Json accountsJson = new Json(true);
            for (final Account account : accounts) {
                final Json accountJson = _toJson(account);
                accountsJson.add(accountJson);
            }
            response.put("accounts", accountsJson);

            _setJsonSuccessFields(response);
            return response;

        } catch (DatabaseException e) {
            _logger.error("Unable to get accounts.", e);
            return _generateErrorJson("Unable to get accounts.");
        }
    }

    protected Json _getAccount(final Account currentAccount, final Long accountId, final Database<Connection> database) throws AuthorizationException {
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

    protected Json _insertAccount(final HttpServletRequest httpServletRequest, final Database<Connection> database) throws IOException {
        final Json response = _generateSuccessJson();
        final Json request = _getRequestDataAsJson(httpServletRequest);
        final Json accountJson = request.get("account");
        final Json companyJson = accountJson.get("company");

        final String username = accountJson.getString("username");
        final String name = accountJson.getString("name");
        final Long companyId = Util.parseLong(companyJson.getString("id"));

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            if (companyId < 1) {
                _logger.error("Unable to insert account: invalid company ID.");
                return _generateErrorJson("Unable to insert account: invalid company ID.");
            }

            final Account account = new Account();
            account.setUsername(username);
            account.setName(name);

            final CompanyInflater companyInflater = new CompanyInflater(databaseConnection);
            final Company company = companyInflater.inflateCompany(companyId);
            account.setCompany(company);

            final DatabaseManager databaseManager = new DatabaseManager(database);
            if (! databaseManager.insertAccount(account)) {
                _logger.error("Unable to insert account: username already exists.");
                return _generateErrorJson("Unable to insert account: username already exists.");
            }

            response.put("accountId", account.getId());
            response.put("password", account.getPassword());
        }
        catch (DatabaseException e) {
            _logger.error("Unable to create account.", e);
            return _generateErrorJson("Unable to create account.");
        }

        return response;
    }

    protected Json _changePassword(final long accountId, final HttpServletRequest request, final Database<Connection> database) throws IOException {
        try {
            final Json jsonRequest = _getRequestDataAsJson(request);
            final Json response = _generateSuccessJson();
            final String oldPassword = jsonRequest.getString("oldPassword");
            final String newPassword = jsonRequest.getString("newPassword");

            if (Util.isBlank(oldPassword)) {
                _logger.error("Unable to change password. Old password is invalid.");
                return _generateErrorJson("Old password is invalid.");
            }
            if (newPassword.length() < 8) {
                _logger.error("Unable to change password. New password is invalid.");
                return _generateErrorJson("New password is invalid.");
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            if (! databaseManager.changePassword(accountId, oldPassword, newPassword)) {
                _logger.error("Unable to change password. Invalid credentials.");
                return _generateErrorJson("Invalid credentials.");
            }

            return response;
        }
        catch (final Exception e) {
            _logger.error("Unable to attempt password change.", e);
            return _generateErrorJson("Unable to attempt password change: " + e.getMessage());
        }
    }

    protected Json _toJson(final Account account) {
        final Json json = new Json(false);

        json.put("id", account.getId());
        json.put("name", account.getName());
        json.put("username", account.getUsername());
        json.put("company", _toJson(account.getCompany()));
        json.put("settings", _toJson(account.getSettings()));
        json.put("roles", _toJson(account.getRoles()));

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

    private Json _toJson(final Collection<Role> roles) {
        final Json json = new Json(true);

        for (final Role role : roles) {
            final Json roleJson = new Json(false);

            final Json permissionsJson = new Json(true);
            for (final Permission permission : role.getPermissions()) {
                permissionsJson.add(permission.name());
            }

            roleJson.put("name", role.getName());
            roleJson.put("permissions", permissionsJson);

            json.add(roleJson);
        }

        return json;
    }
}
