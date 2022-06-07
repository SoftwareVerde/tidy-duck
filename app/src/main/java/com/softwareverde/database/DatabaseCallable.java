package com.softwareverde.database;

public interface DatabaseCallable<ReturnType, ConnectionType> {
    ReturnType call(DatabaseConnection<ConnectionType> databaseConnection) throws DatabaseException;
}
