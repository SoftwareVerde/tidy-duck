package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.http.server.servlet.request.Request;
import com.softwareverde.http.server.servlet.routed.json.AuthenticatedJsonRequestHandler;
import com.softwareverde.http.server.servlet.session.Session;
import com.softwareverde.http.server.servlet.session.SessionManager;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.AccountId;
import com.softwareverde.tidyduck.authentication.TidyDuckAuthenticator;
import com.softwareverde.tidyduck.database.AccountInflater;
import com.softwareverde.tidyduck.environment.TidyDuckEnvironment;

import java.sql.Connection;
import java.util.Map;

public abstract class TidyDuckRequestHandler extends AuthenticatedJsonRequestHandler<TidyDuckEnvironment> {
    protected final SessionManager _sessionManager;
    protected final TidyDuckAuthenticator _authenticator;

    public TidyDuckRequestHandler(final SessionManager sessionManager, final TidyDuckAuthenticator authenticator) {
        _sessionManager = sessionManager;
        _authenticator = authenticator;
    }

    protected abstract Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception;

    @Override
    protected Json handleJsonRequest(final Session session, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = environment.getDatabase().newConnection()) {
            final AccountId accountId = TidyDuckAuthenticator.getAccountId(session);
            final AccountInflater accountInflater = new AccountInflater(databaseConnection);
            final Account currentAccount = accountInflater.inflateAccount(accountId);

            return this.handleRequest(currentAccount, request, environment, parameters);
        }
    }
}
