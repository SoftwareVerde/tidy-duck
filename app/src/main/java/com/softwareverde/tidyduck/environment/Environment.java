package com.softwareverde.tidyduck.environment;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.mysql.MysqlDatabase;
import com.softwareverde.util.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class Environment {

    private static final Logger _logger = LoggerFactory.getLogger(Environment.class);

    private static Environment _environment;
    public static Environment getInstance() throws DatabaseException {
        if (_environment == null) {
             _environment = new Environment();
        }

        return _environment;
    }

    protected Database<Connection> _database;

    protected void _initDatabase() throws DatabaseException {
        final Configuration configuration = new Configuration(IoUtil.getResource(Resource.serverConfigurationFile));
        final Configuration.DatabaseProperties databaseProperties = configuration.getDatabaseProperties();

        final String url = databaseProperties.getConnectionUrl();
        final Integer port = databaseProperties.getPort();
        final String username = databaseProperties.getUsername();
        final String password = databaseProperties.getPassword();
        final String schema = databaseProperties.getSchema();

        final MysqlDatabase mysqlDatabase = new MysqlDatabase(url, username, password);
        mysqlDatabase.setDatabase(schema);

        _database = mysqlDatabase;
    }

    protected Environment() throws DatabaseException {
        _initDatabase();
    }

    public Database<Connection> getDatabase() {
        return _database;
    }

    public static void close(final Connection connection, final Statement statement, final ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (Exception e) {
                _logger.error("Unable to close result set.", e);
            }
        }
        if (statement != null) {
            try {
                connection.close();
            } catch (Exception e) {
                _logger.error("Unable to close statement.", e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                _logger.error("Unable to close connection.", e);
            }
        }
    }

    public static void close(DatabaseConnection databaseConnection) {
        if (databaseConnection != null) {
            try {
                databaseConnection.close();
            } catch (DatabaseException e) {
                _logger.error("Unable to close database connection.", e);
            }
        }
    }
}
