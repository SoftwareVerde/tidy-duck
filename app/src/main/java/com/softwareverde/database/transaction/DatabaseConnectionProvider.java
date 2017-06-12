package com.softwareverde.database.transaction;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;

public interface DatabaseConnectionProvider<T> {
    public DatabaseConnection<T> getNewConnection() throws DatabaseException;
}
