package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.transaction.DatabaseConnectionProvider;
import com.softwareverde.tidyduck.environment.Environment;

import java.sql.Connection;

public class EnvironmentDatabaseConnectionProvider implements DatabaseConnectionProvider<Connection> {
    private final Environment _environment;

    public EnvironmentDatabaseConnectionProvider(Environment environment) {
        _environment = environment;
    }

    @Override
    public DatabaseConnection<Connection> getNewConnection() throws DatabaseException {
        return _environment.getNewDatabaseConnection();
    }
}
