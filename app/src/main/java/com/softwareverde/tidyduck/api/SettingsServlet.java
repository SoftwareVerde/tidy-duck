package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.http.HttpMethod;
import com.softwareverde.http.server.servlet.request.Request;
import com.softwareverde.http.server.servlet.routed.json.AuthenticatedJsonApplicationServlet;
import com.softwareverde.http.server.servlet.routed.json.JsonRequestHandler;
import com.softwareverde.http.server.servlet.session.SessionManager;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Settings;
import com.softwareverde.tidyduck.authentication.TidyDuckAuthenticator;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.environment.TidyDuckEnvironment;
import com.softwareverde.tidyduck.util.Util;

import java.sql.Connection;
import java.util.Map;

public class SettingsServlet extends AuthenticatedJsonApplicationServlet<TidyDuckEnvironment> {
    

    public SettingsServlet(final TidyDuckEnvironment environment, final SessionManager sessionManager, final TidyDuckAuthenticator authenticator) {
        super(environment, sessionManager);
        
        // TODO: consider moving into AccountManagementServlet

        super._defineEndpoint("settings", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                return updateSettings(request, currentAccount, environment);
            }
        });
    }

    private Json updateSettings(final Request request, final Account currentAccount, final TidyDuckEnvironment environment) throws Exception {
        final Database<Connection> database = environment.getDatabase();

        final Json response = JsonRequestHandler.generateSuccessJson();

        try {
            Json jsonRequest = JsonRequestHandler.getRequestDataAsJson(request);

            final String theme = jsonRequest.getString("theme");
            final String defaultMode = jsonRequest.getString("defaultMode");

            if (Util.isBlank(theme)) {
                throw new IllegalArgumentException("Invalid theme: " + theme);
            }
            if (Util.isBlank(defaultMode)) {
                throw new IllegalArgumentException("Invalid default mode: " + defaultMode);
            }

            final Settings settings = new Settings();
            settings.setTheme(theme);
            settings.setDefaultMode(defaultMode);

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.updateAccountSettings(currentAccount.getId(), settings);
        } catch (final Exception exception) {
            final String message = "Unable to update settings.";
            throw new Exception(message, exception);
        }

        return response;
    }
}
