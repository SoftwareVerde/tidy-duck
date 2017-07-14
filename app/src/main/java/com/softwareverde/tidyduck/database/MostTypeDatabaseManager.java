package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;

import java.sql.Connection;

class MostTypeDatabaseManager {

    private final DatabaseConnection<Connection> _databaseConnection;

    public MostTypeDatabaseManager(final DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }
}
