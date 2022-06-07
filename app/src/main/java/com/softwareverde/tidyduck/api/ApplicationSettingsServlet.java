package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.logging.Logger;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Permission;
import com.softwareverde.tidyduck.database.ApplicationSetting;
import com.softwareverde.tidyduck.database.ApplicationSettingsDatabaseManager;
import com.softwareverde.tidyduck.database.ApplicationSettingsInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

public class ApplicationSettingsServlet extends AuthenticatedJsonServlet {

    

    public ApplicationSettingsServlet() {
        super._defineEndpoint("application-settings/<settingName>", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.ADMIN_MODIFY_APPLICATION_SETTINGS);

                final String settingName = parameters.get("settingName");
                final ApplicationSetting applicationSetting = ApplicationSetting.valueOf(settingName);

                return _getSettingValue(applicationSetting, environment.getDatabase());
            }
        });

        super._defineEndpoint("application-settings/<settingName>", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.ADMIN_MODIFY_APPLICATION_SETTINGS);

                final String settingName = parameters.get("settingName");
                final ApplicationSetting applicationSetting = ApplicationSetting.valueOf(settingName);

                return _setSettingValue(request, applicationSetting, environment.getDatabase());
            }
        });
    }

    private Json _getSettingValue(final ApplicationSetting applicationSetting, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final ApplicationSettingsInflater applicationSettingsInflater = new ApplicationSettingsInflater(databaseConnection);
            final String settingValue = applicationSettingsInflater.inflateSetting(applicationSetting);

            final Json response = new Json(false);
            response.put("settingValue", settingValue);

            _setJsonSuccessFields(response);
            return response;
        } catch (DatabaseException e) {
            final String errorMessage = "Unable to get setting value: " + e.getMessage();
            Logger.error(errorMessage, e);
            return _generateErrorJson(errorMessage);
        }
    }

    private Json _setSettingValue(final HttpServletRequest request, final ApplicationSetting applicationSetting, final Database<Connection> database) throws IOException {
        final Json requestJson = _getRequestDataAsJson(request);
        final String settingValue = requestJson.getString("settingValue");

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final ApplicationSettingsDatabaseManager applicationSettingsDatabaseManager = new ApplicationSettingsDatabaseManager(databaseConnection);
            applicationSettingsDatabaseManager.updateSetting(applicationSetting, settingValue);

            return _generateSuccessJson();
        } catch (DatabaseException e) {
            final String errorMessage = "Unable to get setting value: " + e.getMessage();
            Logger.error(errorMessage, e);
            return _generateErrorJson(errorMessage);
        }
    }
}
