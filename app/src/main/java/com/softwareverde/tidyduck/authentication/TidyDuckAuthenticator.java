package com.softwareverde.tidyduck.authentication;

import com.softwareverde.cryptography.argon2.Argon2;
import com.softwareverde.database.jdbc.JdbcDatabase;
import com.softwareverde.database.jdbc.JdbcDatabaseConnection;
import com.softwareverde.database.query.Query;
import com.softwareverde.database.row.Row;
import com.softwareverde.http.server.servlet.request.Request;
import com.softwareverde.http.server.servlet.routed.account.*;
import com.softwareverde.http.server.servlet.routed.json.JsonRequestHandler;
import com.softwareverde.http.server.servlet.session.Session;
import com.softwareverde.json.Json;
import com.softwareverde.logging.Logger;
import com.softwareverde.tidyduck.AccountId;
import com.softwareverde.tidyduck.database.AccountInflater;
import com.softwareverde.tidyduck.environment.TidyDuckEnvironment;

import java.util.List;
import java.util.Map;

public class TidyDuckAuthenticator implements Authenticator<TidyDuckEnvironment> {
    private static String ACCOUNT_ID_PROPERTY = "accountId";
    private final boolean _isTwoFactorEnabled;
    private final PasswordValidator _passwordValidator = new LengthOnlyPasswordValidator();

    public TidyDuckAuthenticator(final boolean isTwoFactorEnabled) {
        _isTwoFactorEnabled = isTwoFactorEnabled;
    }

    @Override
    public AuthenticationResult authenticateUser(final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
        final Json jsonRequest = JsonRequestHandler.getRequestDataAsJson(request);

        final String username = jsonRequest.getString("username");
        if (username == null || username.trim().isEmpty()) {
            return AuthenticationResult.failure("No username provided.");
        }

        final String password = jsonRequest.getString("password");
        if (password == null || password.isEmpty()) {
            return AuthenticationResult.failure("No password provided.");
        }

        final JdbcDatabase database = environment.getDatabase();
        try (final JdbcDatabaseConnection databaseConnection = database.newConnection()) {
            final Query query = new Query("SELECT id, password FROM accounts WHERE username = ?");
            query.setParameter(username.trim());

            final List<Row> rows = databaseConnection.query(query);
            if (rows.size() == 0) {
                return AuthenticationResult.failure("Unable to authenticate.");
            }

            final Row row = rows.get(0);
            final String passwordHash = row.getString("password");

            final Argon2 argon2 = new Argon2(passwordHash);
            final String newHash = argon2.generateParameterizedHash(password.getBytes());
            if (! passwordHash.equals(newHash)) {
                return AuthenticationResult.failure("Unable to authenticate.");
            }

            final AccountId accountId = AccountId.wrap(row.getLong("id"));
            final Json accountJson = new Json(false);
            accountJson.put(ACCOUNT_ID_PROPERTY, accountId);

            return AuthenticationResult.success(accountJson);
        }
        catch (final Exception exception) {
            Logger.error("Exception while validating credentials", exception);
            return AuthenticationResult.failure("Unexpected error during authentication.");
        }
    }

    @Override
    public ResetPasswordResult resetPassword(Session session, Request request, TidyDuckEnvironment environment, Map<String, String> parameters) throws Exception {
        {
            final AccountId accountId = getAccountId(session);
            if (accountId == null) {
                return ResetPasswordResult.failure("Unauthorized.");
            }

            final Json jsonRequest = JsonRequestHandler.getRequestDataAsJson(request);

            final String oldPassword = jsonRequest.getString("oldPassword");
            if (oldPassword == null || oldPassword.isEmpty()) {
                return ResetPasswordResult.failure("Current password not provided.");
            }

            final String password1 = jsonRequest.getString("newPassword1");
            if (password1 == null || password1.isEmpty()) {
                return ResetPasswordResult.failure("New password not provided.");
            }

            final String password2 = jsonRequest.getString("newPassword2");
            if (password2 == null || password2.isEmpty()) {
                return ResetPasswordResult.failure("Confirmation password not provided.");
            }

            if (password1.equals(oldPassword)) {
                return ResetPasswordResult.failure("New password must be different from existing password.");
            }

            if (! password1.equals(password2)) {
                return ResetPasswordResult.failure("New password and confirmation password do not match.");
            }

            final String username = getUsername(session, environment);
            if (username == null) {
                return ResetPasswordResult.failure("Unable to confirm validity of new password.");
            }
            final String newPasswordToLower = password1.toLowerCase();
            final String usernameToLower = username.toLowerCase();
            if (newPasswordToLower.contains(usernameToLower) || usernameToLower.contains(newPasswordToLower)) {
                return ResetPasswordResult.failure("New password is too similar to username.");
            }

            final List<String> errors = _passwordValidator.validatePassword(password1);
            if (errors.size() > 0) {
                return ResetPasswordResult.failure(errors.get(0));
            }

            final JdbcDatabase database = environment.getDatabase();
            try (final JdbcDatabaseConnection databaseConnection = database.newConnection()) {
                final Query currentPasswordHashQuery = new Query("SELECT password FROM accounts WHERE id = ?");
                currentPasswordHashQuery.setParameter(accountId);

                final List<Row> rows = databaseConnection.query(currentPasswordHashQuery);
                if (rows.size() == 0) {
                    // if this ever happened it would presumably mean that the user had an invalid session with a non-existent account ID
                    return ResetPasswordResult.failure("Unable to find account.");
                }

                final Row row = rows.get(0);
                final String passwordHash = row.getString("password");

                final Argon2 oldArgon2 = new Argon2(passwordHash);
                final String confirmationHash = oldArgon2.generateParameterizedHash(oldPassword.getBytes());
                if (! passwordHash.equals(confirmationHash)) {
                    return ResetPasswordResult.failure("Current password does not match.");
                }

                return _updatePassword(databaseConnection, accountId, password1);
            }
            catch (final Exception exception) {
                Logger.error("Exception while validating credentials", exception);
                return ResetPasswordResult.failure("Unexpected error during password reset.");
            }
        }
    }

    @Override
    public AccountInformationResult getAccountInformation(Session session, Request request, TidyDuckEnvironment environment, Map<String, String> parameters) {
        final AccountId accountId = getAccountId(session);
        if (accountId == null) {
            return AccountInformationResult.failure("Unauthorized.");
        }

        final JdbcDatabase database = environment.getDatabase();
        try (final JdbcDatabaseConnection databaseConnection = database.newConnection()) {
            final Query accountsTableQuery = new Query("SELECT requires_password_reset, two_factor_secret IS NOT NULL AS is_two_factor_enabled FROM accounts WHERE accounts.id = ?");
            accountsTableQuery.setParameter(accountId);

            final List<Row> rows = databaseConnection.query(accountsTableQuery);
            if (rows.size() != 1) {
                return AccountInformationResult.failure("Unable to find account with ID " + accountId);
            }

            final Row row = rows.get(0);
            final boolean requiresPasswordReset = row.getBoolean("requires_password_reset");
            final boolean isTwoFactorEnabled = row.getBoolean("is_two_factor_enabled");

            // TODO: revisit whether or not we need two factor authentication.
            final Boolean isTwoFactorAuthenticated = false;
//            final Json sessionJson = session.getMutableData();
//            final Boolean isTwoFactorAuthenticated = sessionJson.getBoolean(TwoFactorAuthenticatedApplicationServlet.TWO_FACTOR_IS_AUTHENTICATED_SESSION_KEY);


            final Json json = new AccountInflater(databaseConnection).inflateAccount(accountId).toJson();
            json.put("requiresPasswordReset", requiresPasswordReset);

            if (_isTwoFactorEnabled) {
                json.put("isTwoFactorEnabled", isTwoFactorEnabled);
                json.put("isTwoFactorAuthenticated", isTwoFactorAuthenticated);
            }
            else {
                // when two factor is disabled, force these values to true so the user always appears authenticated
                json.put("isTwoFactorEnabled", true);
                json.put("isTwoFactorAuthenticated", true);
            }

            return AccountInformationResult.success(json);
        }
        catch (final Exception exception) {
            Logger.error("Unexpected error collecting account information", exception);
            return AccountInformationResult.failure("Unexpected error collecting account information.");
        }

    }

    public static AccountId getAccountId(final Session session) {
        final Json sessionJson = session.getMutableData();
        final Json accountJson = sessionJson.get(LoginRequestHandler.ACCOUNT_SESSION_KEY);
        return AccountId.wrap(accountJson.getOrNull(ACCOUNT_ID_PROPERTY, Json.Types.LONG));
    }

    public static String getUsername(final Session session, final TidyDuckEnvironment environment) {
        final AccountId accountId = getAccountId(session);
        if (accountId == null) {
            return null;
        }
        final JdbcDatabase database = environment.getDatabase();
        try (final JdbcDatabaseConnection databaseConnection = database.newConnection()) {
            final Query query = new Query("SELECT username FROM accounts WHERE id = ?");
            query.setParameter(accountId);

            final List<Row> rows = databaseConnection.query(query);
            if (rows.size() != 1) {
                return null;
            }
            final Row row = rows.get(0);
            return row.getString("username");
        }
        catch (final Exception exception) {
            Logger.error("Unable to get account username for account " + accountId, exception);
            return null;
        }
    }

    private ResetPasswordResult _updatePassword(final JdbcDatabaseConnection databaseConnection, final AccountId accountId, final String newPassword) throws Exception {
        final Argon2 newArgon2 = new Argon2();
        final String newHash = newArgon2.generateParameterizedHash(newPassword.getBytes());

        final Query storePasswordHashQuery = new Query("UPDATE accounts SET password = ?, requires_password_reset = 0 WHERE id = ?");
        storePasswordHashQuery.setParameter(newHash);
        storePasswordHashQuery.setParameter(accountId);

        databaseConnection.executeSql(storePasswordHashQuery);

        return ResetPasswordResult.success();
    }
}
