package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;

import java.sql.Connection;

class MostFunctionStereotypeDatabaseManager {

    private final DatabaseConnection<Connection> _databaseConnection;

    public MostFunctionStereotypeDatabaseManager(final DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }
}
