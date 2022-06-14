package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.http.HttpMethod;
import com.softwareverde.http.server.servlet.request.Request;
import com.softwareverde.http.server.servlet.routed.json.AuthenticatedJsonApplicationServlet;
import com.softwareverde.http.server.servlet.routed.json.JsonRequestHandler;
import com.softwareverde.http.server.servlet.session.SessionManager;
import com.softwareverde.json.Json;
import com.softwareverde.logging.Logger;
import com.softwareverde.tidyduck.*;
import com.softwareverde.tidyduck.authentication.TidyDuckAuthenticator;
import com.softwareverde.tidyduck.database.AccountInflater;
import com.softwareverde.tidyduck.database.CompanyInflater;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.RoleInflater;
import com.softwareverde.tidyduck.environment.TidyDuckEnvironment;
import com.softwareverde.tidyduck.most.Company;
import com.softwareverde.tidyduck.util.Util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AccountManagementServlet extends AuthenticatedJsonApplicationServlet<TidyDuckEnvironment> {
    public AccountManagementServlet(final TidyDuckEnvironment environment, final SessionManager sessionManager, final TidyDuckAuthenticator authenticator) {
        super(environment, sessionManager);
        
        super._defineEndpoint("accounts", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.ADMIN_MODIFY_USERS);

                return _getAccounts(environment.getDatabase());
            }
        });

        super._defineEndpoint("accounts", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.ADMIN_CREATE_USERS);

                return _insertAccount(currentAccount, request, environment.getDatabase());
            }
        });

        super._defineEndpoint("filtered-accounts/active-modify-permission", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.ADMIN_MODIFY_USERS);

                return _getActiveAccountsWithModifyPermission(environment.getDatabase());
            }
        });

        super._defineEndpoint("accounts/<accountId>", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                final AccountId providedAccountId = AccountId.wrap(Util.parseLong(parameters.get("accountId")));
                if (providedAccountId.longValue() < 1) {
                    throw new IllegalArgumentException("Invalid account ID provided.");
                }

                // no permission check, allowing users to see other user's data, barring more private data being added

                return _getAccount(currentAccount, providedAccountId, environment.getDatabase());
            }
        });

        super._defineEndpoint("accounts/<accountId>", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                final AccountId providedAccountId = AccountId.wrap(Util.parseLong(parameters.get("accountId")));
                if (providedAccountId.longValue() < 1) {
                    throw new IllegalArgumentException("Invalid account ID provided.");
                }

                // no permission check, allowing users to see other user's data, barring more private data being added

                return _updateAccountMetadata(currentAccount, providedAccountId, request, environment.getDatabase());
            }
        });

        super._defineEndpoint("accounts/<accountId>/roles", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.ADMIN_MODIFY_USERS);

                final AccountId providedAccountId = AccountId.wrap(Util.parseLong(parameters.get("accountId")));
                if (providedAccountId.longValue() < 1) {
                    throw new IllegalArgumentException("Invalid account ID provided.");
                }
                if (currentAccount.getId().equals(providedAccountId)) {
                    throw new AuthorizationException("Users cannot modify their own roles.");
                }

                return _updateRoles(currentAccount, request, providedAccountId, environment.getDatabase());
            }
        });

        super._defineEndpoint("accounts/<accountId>/change-password", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                final AccountId providedAccountId = AccountId.wrap(Util.parseLong(parameters.get("accountId")));
                if (providedAccountId.longValue() < 1) {
                    throw new IllegalArgumentException("Invalid account ID provided.");
                }

                if (!currentAccount.getId().equals(providedAccountId)) {
                    currentAccount.requirePermission(Permission.ADMIN_MODIFY_USERS);
                }

                return _changePassword(currentAccount, providedAccountId, request, environment.getDatabase());
            }
        });

        super._defineEndpoint("accounts/<accountId>/reset-password", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                final AccountId providedAccountId = AccountId.wrap(Util.parseLong(parameters.get("accountId")));
                if (providedAccountId.longValue() < 1) {
                    throw new IllegalArgumentException("Invalid account ID provided.");
                }

                currentAccount.requirePermission(Permission.ADMIN_MODIFY_USERS);

                return _resetPassword(currentAccount, providedAccountId, environment.getDatabase());
            }
        });

        super._defineEndpoint("accounts/<accountId>/delete-account", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                final AccountId providedAccountId = AccountId.wrap(Util.parseLong(parameters.get("accountId")));
                if (providedAccountId.longValue() < 1) {
                    throw new IllegalArgumentException("Invalid account ID provided.");
                }

                currentAccount.requirePermission(Permission.ADMIN_MODIFY_USERS);

                return _markAccountAsDeleted(currentAccount, providedAccountId, environment.getDatabase());
            }
        });


        super._defineEndpoint("companies", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                return _getCompanies(environment.getDatabase());
            }
        });

        super._defineEndpoint("companies", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                return _insertCompany(currentAccount, request, environment.getDatabase());
            }
        });
    }

    private Json _getAccounts(final Database<Connection> database) throws Exception {
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

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;

        } catch (final DatabaseException exception) {
            throw new Exception("Unable to get accounts.", exception);
        }
    }

    private Json _getActiveAccountsWithModifyPermission(final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final AccountInflater accountInflater = new AccountInflater(databaseConnection);
            final List<Account> accounts = accountInflater.inflateAccounts();

            final Json response = new Json(false);

            final Json accountsJson = new Json(true);
            for (final Account account : accounts) {
                if (account.hasPermission(Permission.LOGIN)) {
                    if (account.hasPermission(Permission.MOST_COMPONENTS_MODIFY)) {
                        final Json accountJson = _toJson(account);
                        accountsJson.add(accountJson);
                    }
                }
            }
            response.put("accounts", accountsJson);

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;

        } catch (final DatabaseException exception) {
            throw new Exception("Unable to get accounts.", exception);
        }
    }

    protected Json _getAccount(final Account currentAccount, final AccountId accountId, final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final AccountInflater accountInflater = new AccountInflater(databaseConnection);
            final Account account = accountInflater.inflateAccount(accountId);

            final Json response = new Json(false);

            response.put("account", _toJson(account));

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;

        } catch (final DatabaseException exception) {
            throw new Exception("Unable to get account.", exception);
        }
    }

    protected Json _getCompanies(final Database<Connection> database) throws Exception {
        final Json response = JsonRequestHandler.generateSuccessJson();
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
        catch (final DatabaseException exception) {
            throw new Exception("Unable to get companies from database.", exception);
        }
    }

    protected Json _insertCompany(final Account currentAccount, final Request request, final Database<Connection> database) throws Exception {
        final Json response = JsonRequestHandler.generateSuccessJson();
        final Json requestJson = JsonRequestHandler.getRequestDataAsJson(request);
        final Json companyJson = requestJson.get("company");
        final String companyName = companyJson.getString("name");

        if (Util.isBlank(companyName)) {
            Logger.error("Unable to insert company: invalid company name.");
            throw new IllegalArgumentException("Unable to insert company: invalid company name.");
        }

        try {
            final Company company = new Company();
            company.setName(companyName);

            final DatabaseManager databaseManager = new DatabaseManager(database);
            if (! databaseManager.insertCompany(company)) {
                Logger.error("Unable to insert company: company name already exists.");
                throw new IllegalArgumentException("Unable to insert company: company name already exists.");
            }

            response.put("companyId", company.getId());
            Logger.info("User " + currentAccount.getId() + " created company " + company.getId());
            return response;
        }
        catch (final DatabaseException exception) {
            throw new Exception("Unable to create company", exception);
        }
    }

    protected Json _insertAccount(final Account currentAccount, final Request Request, final Database<Connection> database) throws Exception {
        final Json response = JsonRequestHandler.generateSuccessJson();
        final Json request = JsonRequestHandler.getRequestDataAsJson(Request);
        final Json accountJson = request.get("account");
        final Json companyJson = accountJson.get("company");
        final Json rolesJson = accountJson.get("roles");

        final String username = accountJson.getString("username");
        final String name = accountJson.getString("name");
        final Long companyId = Util.parseLong(companyJson.getString("id"));

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            if (companyId < 1) {
                Logger.error("Unable to insert account: invalid company ID.");
                throw new IllegalArgumentException("Unable to insert account: invalid company ID.");
            }

            final Account account = new Account();
            account.setUsername(username);
            account.setName(name);

            final CompanyInflater companyInflater = new CompanyInflater(databaseConnection);
            final Company company = companyInflater.inflateCompany(companyId);
            account.setCompany(company);

            final RoleInflater roleInflater = new RoleInflater(databaseConnection);

            final List<String> roleNames = new ArrayList<>(rolesJson.length());
            for (int i=0; i<rolesJson.length(); i++) {
                final Json roleJson = rolesJson.get(i);
                final Role role = roleInflater.inflateRoleFromName(roleJson.getString("name"));
                account.addRole(role);
                roleNames.add(role.getName());
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            if (! databaseManager.insertAccount(account)) {
                throw new Exception("Unable to insert account: username already exists.");
            }

            response.put("accountId", account.getId());
            response.put("password", account.getPassword());

            Logger.info("User " + currentAccount.getId() + " created account " + account.getId() + " with company " + company.getId() + " and roles " + roleNames.toString());
        }
        catch (final DatabaseException exception) {
            throw new Exception("Unable to create account.", exception);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    protected Json _updateAccountMetadata(final Account currentAccount, final AccountId accountId, final Request Request, final Database<Connection> database) throws Exception {
        final Json response = JsonRequestHandler.generateSuccessJson();
        final Json request = JsonRequestHandler.getRequestDataAsJson(Request);
        final Json accountJson = request.get("account");
        final Json companyJson = accountJson.get("company");

        final String username = accountJson.getString("username");
        final String name = accountJson.getString("name");
        final Long companyId = Util.parseLong(companyJson.getString("id"));

        if (accountId.longValue() < 1) {
            Logger.error("Unable to update account: invalid account ID.");
            throw new IllegalArgumentException("Unable to update account: invalid account ID.");
        }
        if (Util.isBlank(username)) {
            Logger.error("Unable to update account: invalid username.");
            throw new IllegalArgumentException("Unable to update account: invalid username.");
        }
        if (Util.isBlank(name)) {
            Logger.error("Unable to update account: invalid name.");
            throw new IllegalArgumentException("Unable to update account: invalid name.");
        }
        if (companyId < 1) {
            Logger.error("Unable to update account: invalid company ID.");
            throw new IllegalArgumentException("Unable to update account: invalid company ID.");
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
                Logger.error("Unable to update account: username already exists.");
                throw new IllegalArgumentException("Unable to update account: username already exists.");
            }
            Logger.info("User " + currentAccount.getId() + " updated account " + account.getId() + " with the following new information: " + accountJson.toString());
        }
        catch (final DatabaseException exception) {
            throw new Exception("Unable to update account.", exception);
        }

        return response;
    }

    private Json _updateRoles(final Account currentAccount, final Request request, final AccountId providedAccountId, final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json jsonRequest = JsonRequestHandler.getRequestDataAsJson(request);
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

            Logger.info("User " + currentAccount.getId() + " changed user " + providedAccountId + "'s roles to " + rolesJson.toString());

            final Json response = JsonRequestHandler.generateSuccessJson();
            return response;
        }
        catch (final Exception exception) {
            throw new Exception("Unable to attempt password change.", exception);
        }
    }

    protected Json _changePassword(final Account currentAccount, final AccountId accountId, final Request request, final Database<Connection> database) throws Exception {
        try {
            final Json jsonRequest = JsonRequestHandler.getRequestDataAsJson(request);
            final Json response = JsonRequestHandler.generateSuccessJson();
            final String oldPassword = jsonRequest.getString("oldPassword");
            final String newPassword = jsonRequest.getString("newPassword");

            if (Util.isBlank(oldPassword)) {
                Logger.error("Unable to change password. Old password is invalid.");
                throw new IllegalArgumentException("Old password is invalid.");
            }
            if (newPassword.length() < 8) {
                Logger.error("Unable to change password. New password is invalid.");
                throw new IllegalArgumentException("New password is invalid.");
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            if (! databaseManager.changePassword(accountId, oldPassword, newPassword)) {
                Logger.error("Unable to change password. Invalid credentials.");
                throw new IllegalArgumentException("Invalid credentials.");
            }

            Logger.info("User " + currentAccount.getId() + " changed user " + accountId + "'s password.");
            return response;
        }
        catch (final Exception exception) {
            throw new Exception("Unable to attempt password change.", exception);
        }
    }

    protected Json _resetPassword(final Account currentAccount, final AccountId accountId, final Database<Connection> database) throws Exception {
        final Json response = JsonRequestHandler.generateSuccessJson();
        try{
            final DatabaseManager databaseManager = new DatabaseManager(database);
            final String newPassword = databaseManager.resetPassword(accountId);

            response.put("newPassword", newPassword);

            Logger.info("User " + currentAccount.getId() + " reset user " + accountId + "'s password.");
            return response;
        }
        catch (final DatabaseException exception) {
            throw new Exception("Unable to reset password", exception);
        }
    }

    protected Json _markAccountAsDeleted(final Account currentAccount, final AccountId accountId, final Database<Connection> database) throws Exception {
        final Json response = JsonRequestHandler.generateSuccessJson();
        try{
            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.markAccountAsDeleted(accountId);

            Logger.info("User " + currentAccount.getId() + " marked user " + accountId + " as deleted.");
            return response;
        }
        catch (final DatabaseException exception) {
            throw new Exception("Unable to mark account as deleted", exception);
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
