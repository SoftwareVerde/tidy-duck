package com.softwareverde.tidyduck.environment;

import com.softwareverde.tidyduck.R;
import com.softwareverde.util.IoUtil;

import java.sql.Connection;

public class Environment {
    private static Environment _environment = new Environment();

    public static Environment getInstance() {
        return _environment;
    }

    protected Connection _databaseConnection;

    protected Environment() {
        final Configuration configuration = new Configuration(IoUtil.getResource(R.serverConfigurationFile));
        final Configuration.DatabaseProperties databaseProperties = configuration.getDatabaseProperties();


    }

    public Connection getNewDatabaseConnection() {
        return _databaseConnection;
    }
}
