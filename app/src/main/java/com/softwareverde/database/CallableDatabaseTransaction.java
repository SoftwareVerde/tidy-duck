package com.softwareverde.database;

import com.softwareverde.database.transaction.JdbcDatabaseTransaction;

import java.sql.Connection;
import java.sql.SQLException;

public class CallableDatabaseTransaction<T> extends JdbcDatabaseTransaction {

    public CallableDatabaseTransaction(final Database<Connection> database) {
        super(database);
    }

    public T call(DatabaseCallable<T, Connection> databaseConnectedCallable) throws DatabaseException {
        try {
            DatabaseConnection<Connection> databaseConnection = this._database.newConnection();
            Throwable throwable = null;

            try {
                Connection connection = (Connection)databaseConnection.getRawConnection();

                try {
                    connection.setAutoCommit(false);
                    T returnValue = databaseConnectedCallable.call(databaseConnection);
                    connection.commit();
                    return returnValue;
                } catch (SQLException exception) {
                    connection.rollback();
                    throw exception;
                }
            } catch (Throwable t) {
                throwable = t;
                throw t;
            } finally {
                if (databaseConnection != null) {
                    if (throwable != null) {
                        try {
                            databaseConnection.close();
                        } catch (Throwable t) {
                            throwable.addSuppressed(t);
                        }
                    } else {
                        databaseConnection.close();
                    }
                }

            }
        } catch (Exception exception) {
            throw new DatabaseException("Unable to complete query.", exception);
        }
    }
}
