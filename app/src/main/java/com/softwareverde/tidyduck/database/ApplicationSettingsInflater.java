package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.database.row.Row;

import java.sql.Connection;
import java.util.List;

public class ApplicationSettingsInflater {
    protected final DatabaseConnection<Connection> _databaseConnection;

    public ApplicationSettingsInflater(DatabaseConnection<Connection> connection) {
        _databaseConnection = connection;
    }

    public String inflateSetting(final ApplicationSetting applicationSetting) throws DatabaseException {
        final Query query = new Query("SELECT value FROM application_settings WHERE name = ?")
                .setParameter(applicationSetting.getSettingName())
                ;

        List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Unable to find setting: " + applicationSetting.getSettingName());
        }
        String value = rows.get(0).getString("value");
        return value;
    }
}
