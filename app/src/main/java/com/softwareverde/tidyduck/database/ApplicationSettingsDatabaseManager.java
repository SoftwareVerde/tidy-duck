package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;

import java.sql.Connection;

public class ApplicationSettingsDatabaseManager {
    private final DatabaseConnection<Connection> _databaseConnection;

    public ApplicationSettingsDatabaseManager(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public void updateSetting(final ApplicationSetting applicationSetting, final String value) throws DatabaseException {
        final Query query = new Query("UPDATE application_settings SET value = ? WHERE name = ?")
                .setParameter(value)
                .setParameter(applicationSetting.getSettingName())
                ;

        _databaseConnection.executeSql(query);
    }
}
