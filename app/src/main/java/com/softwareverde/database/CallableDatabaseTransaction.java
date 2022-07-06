package com.softwareverde.database;

import com.softwareverde.database.jdbc.transaction.JdbcDatabaseTransaction;

import java.sql.Connection;
import java.sql.SQLException;

public class CallableDatabaseTransaction<T> extends JdbcDatabaseTransaction {
    public CallableDatabaseTransaction(final Database<Connection> database) {
        super(database);
    }

    public T call(DatabaseCallable<T, Connection> databaseConnectedCallable) throws DatabaseException {
        try (final DatabaseConnection<Connection> databaseConnection = this._databaseConnectionFactory.newConnection()) {
            try (final Connection connection = databaseConnection.getRawConnection()){
                try {
                    connection.setAutoCommit(false);
                    final T returnValue = databaseConnectedCallable.call(databaseConnection);
                    connection.commit();
                    return returnValue;
                } catch (SQLException exception) {
                    connection.rollback();
                    throw exception;
                }
            }
        } catch (Exception exception) {
            throw new DatabaseException("Unable to complete query.", exception);
        }
    }
}
