package com.softwareverde.database.transaction;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.tidyduck.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class JdbcDatabaseTransaction implements DatabaseTransaction<Connection> {

    private final Logger _logger = LoggerFactory.getLogger(getClass());
    private final DatabaseConnectionProvider<Connection> _databaseConnectionProvider;

    public JdbcDatabaseTransaction(DatabaseConnectionProvider<Connection> databaseConnectionProvider) {
        _databaseConnectionProvider = databaseConnectionProvider;
    }

    @Override
    public void execute(DatabaseConnectedRunnable<Connection> databaseConnectedRunnable) throws DatabaseException {
        DatabaseConnection<Connection> databaseConnection = null;
        Connection connection = null;
        try {
            databaseConnection = _databaseConnectionProvider.getNewDatabaseConnection();
            connection = databaseConnection.getRawConnection();
            connection.setAutoCommit(false);

            databaseConnectedRunnable.run(databaseConnection);

            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    _logger.error("Unable to rollback database connection.");
                }
            }
            throw new DatabaseException("Unable to complete action.", e);
        } finally {
            Environment.close(databaseConnection);
        }
    }
}
