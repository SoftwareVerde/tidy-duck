package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.http.server.servlet.request.Request;
import com.softwareverde.http.server.servlet.response.Response;
import com.softwareverde.http.server.servlet.routed.json.AuthenticatedJsonRequestHandler;
import com.softwareverde.http.server.servlet.session.Session;
import com.softwareverde.http.server.servlet.session.SessionManager;
import com.softwareverde.json.Json;
import com.softwareverde.logging.Logger;
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
            if (accountId == null) {
                throw new Exception("Invalid session.");
            }

            final AccountInflater accountInflater = new AccountInflater(databaseConnection);
            final Account currentAccount = accountInflater.inflateAccount(accountId);

            return this.handleRequest(currentAccount, request, environment, parameters);
        }
    }

    @Override
    public Response handleRequest(final Session session, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
        final Response response = new Response();
        response.addHeader(Response.Headers.CONTENT_TYPE, "application/json");

        Json json;
        try {
            json = handleJsonRequest(session, request, environment, parameters);

            if (json == null) {
                json = AuthenticatedJsonRequestHandler.generateErrorJson("Unable to determine response");
            }
        }
        catch (final IllegalArgumentException exception) {
            final String msg = "Invalid or missing request content provided: ";
            Logger.error(msg, exception);
            json = AuthenticatedJsonRequestHandler.generateErrorJson(msg + ": " + exception.getMessage());

            response.setCode(Response.Codes.BAD_REQUEST);
            response.setContent(json.toString());
            return response;
        }
        catch (final Exception exception) {
            String msg = "Unable to handle request";
            Logger.error(msg, exception);
            json = AuthenticatedJsonRequestHandler.generateErrorJson(msg + ": " + exception.getMessage());

            response.setCode(Response.Codes.SERVER_ERROR);
            response.setContent(json.toString());
            return response;
        }

        response.setCode(Response.Codes.OK);
        response.setContent(json.toString());
        return response;
    }
}
