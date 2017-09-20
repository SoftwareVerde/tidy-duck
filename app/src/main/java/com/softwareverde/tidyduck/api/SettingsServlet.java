package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Settings;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.Map;

public class SettingsServlet extends AuthenticatedJsonServlet {
    private Logger _logger = LoggerFactory.getLogger(getClass());

    public SettingsServlet() {
        // TODO: consider moving into AccountManagementServlet

        super.defineEndpoint("settings", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                return updateSettings(request, currentAccount, environment);
            }
        });
    }

    private Json updateSettings(final HttpServletRequest request, final Account currentAccount, final Environment environment) {
        final Database<Connection> database = environment.getDatabase();

        Json response = super._generateSuccessJson();

        try {
            Json jsonRequest = _getRequestDataAsJson(request);

            String theme = jsonRequest.getString("theme");

            if (Util.isBlank(theme)) {
                return super._generateErrorJson("Invalid theme: " + theme);
            }

            final Settings settings = new Settings();
            settings.setTheme(theme);

            DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.updateAccountSettings(currentAccount.getId(), settings);
        } catch (Exception e) {
            String message = "Unable to update settings.";
            _logger.error(message, e);
            return super._generateErrorJson(message);
        }

        return response;
    }
}
