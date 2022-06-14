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
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Permission;
import com.softwareverde.tidyduck.authentication.TidyDuckAuthenticator;
import com.softwareverde.tidyduck.database.ApplicationSetting;
import com.softwareverde.tidyduck.database.ApplicationSettingsDatabaseManager;
import com.softwareverde.tidyduck.database.ApplicationSettingsInflater;
import com.softwareverde.tidyduck.environment.TidyDuckEnvironment;

import java.sql.Connection;
import java.util.Map;

public class ApplicationSettingsServlet extends AuthenticatedJsonApplicationServlet<TidyDuckEnvironment> {
    public ApplicationSettingsServlet(final TidyDuckEnvironment environment, final SessionManager sessionManager, final TidyDuckAuthenticator authenticator) {
        super(environment, sessionManager);
        
        super._defineEndpoint("application-settings/<settingName>", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.ADMIN_MODIFY_APPLICATION_SETTINGS);

                final String settingName = parameters.get("settingName");
                final ApplicationSetting applicationSetting = ApplicationSetting.valueOf(settingName);

                return _getSettingValue(applicationSetting, environment.getDatabase());
            }
        });

        super._defineEndpoint("application-settings/<settingName>", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.ADMIN_MODIFY_APPLICATION_SETTINGS);

                final String settingName = parameters.get("settingName");
                final ApplicationSetting applicationSetting = ApplicationSetting.valueOf(settingName);

                return _setSettingValue(request, applicationSetting, environment.getDatabase());
            }
        });
    }

    private Json _getSettingValue(final ApplicationSetting applicationSetting, final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final ApplicationSettingsInflater applicationSettingsInflater = new ApplicationSettingsInflater(databaseConnection);
            final String settingValue = applicationSettingsInflater.inflateSetting(applicationSetting);

            final Json response = new Json(false);
            response.put("settingValue", settingValue);

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        } catch (DatabaseException exception) {
            final String errorMessage = "Unable to get setting value: " + exception.getMessage();
            throw new Exception(errorMessage, exception);
        }
    }

    private Json _setSettingValue(final Request request, final ApplicationSetting applicationSetting, final Database<Connection> database) throws Exception {
        final Json requestJson = JsonRequestHandler.getRequestDataAsJson(request);
        final String settingValue = requestJson.getString("settingValue");

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final ApplicationSettingsDatabaseManager applicationSettingsDatabaseManager = new ApplicationSettingsDatabaseManager(databaseConnection);
            applicationSettingsDatabaseManager.updateSetting(applicationSetting, settingValue);

            return JsonRequestHandler.generateSuccessJson();
        } catch (DatabaseException exception) {
            final String errorMessage = "Unable to get setting value: " + exception.getMessage();
            throw new Exception(errorMessage, exception);
        }
    }
}
