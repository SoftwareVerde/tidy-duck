package com.softwareverde.tidyduck.api;

import com.softwareverde.database.*;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Settings;
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
import java.util.List;
import java.util.Map;

public class AccountManagementServlet extends AuthenticatedJsonServlet {
    private Logger _logger = LoggerFactory.getLogger(getClass());
    
    public AccountManagementServlet() {
        super.defineEndpoint("accounts/<accountId>", HttpMethod.GET, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final Long providedAccountId = Util.parseLong(parameters.get("accountId"));
                if (providedAccountId < 1) {
                    return _generateErrorJson("Invalid account ID provided.");
                }
                return _getAccount(providedAccountId, environment.getDatabase());
            }
        });

        super.defineEndpoint("accounts/<accountId>/change-password", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final Long providedAccountId = Util.parseLong(parameters.get("accountId"));
                if (providedAccountId < 1) {
                    return _generateErrorJson("Invalid account ID provided.");
                }
                return _changePassword(providedAccountId, request, environment.getDatabase());
            }
        });

        super.defineEndpoint("accounts/create", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                return _insertAccount(request, environment.getDatabase());
            }
        });

        super.defineEndpoint("accounts/companies/get-all", HttpMethod.GET, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                return _getCompanies(environment.getDatabase());
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

    protected Json _getCompanies(final Database<Connection> database) throws DatabaseException {
        final Json response = _generateSuccessJson();
        final Json companiesJson = new Json();

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final CompanyInflater companyInflater = new CompanyInflater(databaseConnection);
            final List<Company> companies = companyInflater.inflateAllCompanies();

            for (final Company company : companies) {
                final Json companyJson = _toJson(company);
                companiesJson.add(companyJson);
            }

            response.put("companies", companiesJson);
            return response;
        }
        catch (DatabaseException e) {
            _logger.error("Unable to get companies from database.", e);
            return _generateErrorJson("Unable to get companies from database.");
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

    protected Json _changePassword(final Long accountId, final HttpServletRequest request, final Database<Connection> database) throws IOException {
        final Json jsonRequest = _getRequestDataAsJson(request);
        final Json response = _generateSuccessJson();
        final String oldPassword = jsonRequest.getString("oldPassword");
        final String newPassword = jsonRequest.getString("newPassword");

        try {
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
