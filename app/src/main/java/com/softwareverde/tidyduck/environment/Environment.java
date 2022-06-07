package com.softwareverde.tidyduck.environment;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.mysql.MysqlDatabase;
import com.softwareverde.database.properties.DatabaseProperties;
import com.softwareverde.database.properties.MutableDatabaseProperties;
import com.softwareverde.logging.Logger;
import com.softwareverde.util.IoUtil;


import java.sql.*;

public class Environment {
    private static Environment _environment;
    public static Environment getInstance() {
        if (_environment == null) {
             _environment = new Environment();
        }

        return _environment;
    }

    protected Database<Connection> _database;

    protected void _initDatabase() {
        final Configuration configuration = new Configuration(IoUtil.getResource(Resource.serverConfigurationFile));
        final Configuration.DatabaseProperties configurationDatabaseProperties = configuration.getDatabaseProperties();

        final String url = configurationDatabaseProperties.getConnectionUrl();
        final Integer port = configurationDatabaseProperties.getPort();
        final String username = configurationDatabaseProperties.getUsername();
        final String password = configurationDatabaseProperties.getPassword();
        final String schema = configurationDatabaseProperties.getSchema();

        final MutableDatabaseProperties databaseProperties = new MutableDatabaseProperties();
        databaseProperties.setPort(port);
        databaseProperties.setHostname(url);
        databaseProperties.setUsername(username);
        databaseProperties.setPassword(password);
        databaseProperties.setSchema(schema);

        final MysqlDatabase mysqlDatabase = new MysqlDatabase(databaseProperties);

        _database = mysqlDatabase;
    }

    protected Environment() {
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
                Logger.error("Unable to close result set.", e);
            }
        }
        if (statement != null) {
            try {
                connection.close();
            } catch (Exception e) {
                Logger.error("Unable to close statement.", e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                Logger.error("Unable to close connection.", e);
            }
        }
    }

    public static void close(DatabaseConnection databaseConnection) {
        if (databaseConnection != null) {
            try {
                databaseConnection.close();
            } catch (DatabaseException e) {
                Logger.error("Unable to close database connection.", e);
            }
        }
    }
}
