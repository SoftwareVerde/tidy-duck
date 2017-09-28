package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.*;
import com.softwareverde.tidyduck.database.AccountInflater;
import com.softwareverde.tidyduck.database.CompanyInflater;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.RoleInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.Company;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AccountManagementServlet extends AuthenticatedJsonServlet {
    private Logger _logger = LoggerFactory.getLogger(getClass());

    public AccountManagementServlet() {
        super._defineEndpoint("accounts", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.ADMIN_MODIFY_USERS);

                return _getAccounts(environment.getDatabase());
            }
        });

        super._defineEndpoint("accounts", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.ADMIN_CREATE_USERS);

                return _insertAccount(currentAccount, request, environment.getDatabase());
            }
        });

        super._defineEndpoint("accounts/<accountId>", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                final Long providedAccountId = Util.parseLong(parameters.get("accountId"));
                if (providedAccountId < 1) {
                    return _generateErrorJson("Invalid account ID provided.");
                }

                // no permission check, allowing users to see other user's data, barring more private data being added

                return _getAccount(currentAccount, providedAccountId, environment.getDatabase());
            }
        });

        super._defineEndpoint("accounts/<accountId>", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                final Long providedAccountId = Util.parseLong(parameters.get("accountId"));
                if (providedAccountId < 1) {
                    return _generateErrorJson("Invalid account ID provided.");
                }

                // no permission check, allowing users to see other user's data, barring more private data being added

                return _updateAccountMetadata(currentAccount, providedAccountId, request, environment.getDatabase());
            }
        });

        super._defineEndpoint("accounts/<accountId>/roles", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.ADMIN_MODIFY_USERS);

                final Long providedAccountId = Util.parseLong(parameters.get("accountId"));
                if (providedAccountId < 1) {
                    return _generateErrorJson("Invalid account ID provided.");
                }
                if (currentAccount.getId().equals(providedAccountId)) {
                    throw new AuthorizationException("Users cannot modify their own roles.");
                }

                return _updateRoles(currentAccount, request, providedAccountId, environment.getDatabase());
            }
        });

        super._defineEndpoint("accounts/<accountId>/change-password", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                final Long providedAccountId = Util.parseLong(parameters.get("accountId"));
                if (providedAccountId < 1) {
                    return _generateErrorJson("Invalid account ID provided.");
                }

                if (!currentAccount.getId().equals(providedAccountId)) {
                    currentAccount.requirePermission(Permission.ADMIN_MODIFY_USERS);
                }

                return _changePassword(currentAccount, providedAccountId, request, environment.getDatabase());
            }
        });

        super._defineEndpoint("accounts/<accountId>/reset-password", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                final Long providedAccountId = Util.parseLong(parameters.get("accountId"));
                if (providedAccountId < 1) {
                    return _generateErrorJson("Invalid account ID provided.");
                }

                currentAccount.requirePermission(Permission.ADMIN_MODIFY_USERS);

                return _resetPassword(currentAccount, providedAccountId, environment.getDatabase());
            }
        });

        super._defineEndpoint("companies", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                return _getCompanies(environment.getDatabase());
            }
        });

        super._defineEndpoint("companies", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                return _insertCompany(currentAccount, request, environment.getDatabase());
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

    protected Json _insertCompany(final Account currentAccount, final HttpServletRequest request, final Database<Connection> database) throws IOException {
        final Json response = _generateSuccessJson();
        final Json requestJson = _getRequestDataAsJson(request);
        final Json companyJson = requestJson.get("company");
        final String companyName = companyJson.getString("name");

        if (Util.isBlank(companyName)) {
            _logger.error("Unable to insert company: invalid company name.");
            return _generateErrorJson("Unable to insert company: invalid company name.");
        }

        try {
            final Company company = new Company();
            company.setName(companyName);

            final DatabaseManager databaseManager = new DatabaseManager(database);
            if (! databaseManager.insertCompany(company)) {
                _logger.error("Unable to insert company: company name already exists.");
                return _generateErrorJson("Unable to insert company: company name already exists.");
            }

            response.put("companyId", company.getId());
            _logger.info("User " + currentAccount.getId() + " created company " + company.getId());
            return response;
        }
        catch (DatabaseException e) {
            _logger.error("Unable to create company: ", e);
            return _generateErrorJson("Unable to create company: " + e.getMessage());
        }
    }

    protected Json _insertAccount(final Account currentAccount, final HttpServletRequest httpServletRequest, final Database<Connection> database) throws IOException {
        final Json response = _generateSuccessJson();
        final Json request = _getRequestDataAsJson(httpServletRequest);
        final Json accountJson = request.get("account");
        final Json companyJson = accountJson.get("company");
        final Json rolesJson = accountJson.get("roles");

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

            final RoleInflater roleInflater = new RoleInflater(databaseConnection);

            for (int i=0; i<rolesJson.length(); i++) {
                final Json roleJson = rolesJson.get(i);
                final Role role = roleInflater.inflateRoleFromName(roleJson.getString("name"));
                account.addRole(role);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            if (! databaseManager.insertAccount(account)) {
                _logger.error("Unable to insert account: username already exists.");
                return _generateErrorJson("Unable to insert account: username already exists.");
            }

            response.put("accountId", account.getId());
            response.put("password", account.getPassword());
            // TODO: add roles to log statement
            _logger.info("User " + currentAccount.getId() + " created account " + account.getId() + " with company " + company.getId());
        }
        catch (DatabaseException e) {
            _logger.error("Unable to create account.", e);
            return _generateErrorJson("Unable to create account.");
        }

        return response;
    }

    protected Json _updateAccountMetadata(final Account currentAccount, final long accountId, final HttpServletRequest httpServletRequest, final Database<Connection> database) throws IOException {
        final Json response = _generateSuccessJson();
        final Json request = _getRequestDataAsJson(httpServletRequest);
        final Json accountJson = request.get("account");
        final Json companyJson = accountJson.get("company");

        final String username = accountJson.getString("username");
        final String name = accountJson.getString("name");
        final Long companyId = Util.parseLong(companyJson.getString("id"));

        if (accountId < 1) {
            _logger.error("Unable to update account: invalid account ID.");
            return _generateErrorJson("Unable to update account: invalid account ID.");
        }
        if (Util.isBlank(username)) {
            _logger.error("Unable to update account: invalid username.");
            return _generateErrorJson("Unable to update account: invalid username.");
        }
        if (Util.isBlank(name)) {
            _logger.error("Unable to update account: invalid name.");
            return _generateErrorJson("Unable to update account: invalid name.");
        }
        if (companyId < 1) {
            _logger.error("Unable to update account: invalid company ID.");
            return _generateErrorJson("Unable to update account: invalid company ID.");
        }

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final AccountInflater accountInflater = new AccountInflater(databaseConnection);
            final String existingAccountUsername = accountInflater.inflateAccount(accountId).getUsername();
            final boolean isNewUsernameDifferent = ! username.equals(existingAccountUsername);

            final Account account = new Account();
            account.setId(accountId);
            account.setName(name);
            account.setUsername(username);

            final Company company = new Company();
            company.setId(companyId);
            account.setCompany(company);

            final DatabaseManager databaseManager = new DatabaseManager(database);
            if (! databaseManager.updateAccountMetadata(account, isNewUsernameDifferent)) {
                _logger.error("Unable to update account: username already exists.");
                return _generateErrorJson("Unable to update account: username already exists.");
            }
            _logger.info("User " + currentAccount.getId() + " updated account " + account.getId() + " with the following new information: " + accountJson.toString());
        }
        catch (DatabaseException e) {
            _logger.error("Unable to update account.", e);
            return _generateErrorJson("Unable to update account: " + e.getMessage());
        }

        return response;
    }

    private Json _updateRoles(final Account currentAccount, final HttpServletRequest request, final Long providedAccountId, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json jsonRequest = _getRequestDataAsJson(request);
            final Json rolesJson = jsonRequest.get("roleNames");

            final RoleInflater roleInflater = new RoleInflater(databaseConnection);

            List<Role> roles = new ArrayList<>();
            for (int i=0; i<rolesJson.length(); i++) {
                final String roleName = rolesJson.getString(i);
                final Role role = roleInflater.inflateRoleFromName(roleName);
                roles.add(role);
            }

            DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.updateAccountRoles(providedAccountId, roles);

            _logger.info("User " + currentAccount.getId() + " changed user " + providedAccountId + "'s roles to " + rolesJson.toString());

            final Json response = _generateSuccessJson();
            return response;
        }
        catch (final Exception e) {
            _logger.error("Unable to attempt password change.", e);
            return _generateErrorJson("Unable to attempt password change: " + e.getMessage());
        }
    }

    protected Json _changePassword(final Account currentAccount, final long accountId, final HttpServletRequest request, final Database<Connection> database) throws IOException {
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

            _logger.info("User " + currentAccount.getId() + " changed user " + accountId + "'s password.");
            return response;
        }
        catch (final Exception e) {
            _logger.error("Unable to attempt password change.", e);
            return _generateErrorJson("Unable to attempt password change: " + e.getMessage());
        }
    }

    protected Json _resetPassword(final Account currentAccount, final long accountId, final Database<Connection> database) {
        final Json response = _generateSuccessJson();
        try{
            final DatabaseManager databaseManager = new DatabaseManager(database);
            final String newPassword = databaseManager.resetPassword(accountId);

            response.put("newPassword", newPassword);

            _logger.info("User " + currentAccount.getId() + " reset user " + accountId + "'s password.");
            return response;
        }
        catch (DatabaseException e) {
            _logger.error("Unable to reset password: ", e);
            return _generateErrorJson("Unable to reset password: " + e.getMessage());
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
        json.put("defaultMode", settings.getDefaultMode());

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

            roleJson.put("id", role.getId());
            roleJson.put("name", role.getName());
            roleJson.put("permissions", permissionsJson);

            json.add(roleJson);
        }

        return json;
    }
}
