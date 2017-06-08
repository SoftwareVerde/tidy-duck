package com.softwareverde.tidyduck.environment;

import com.softwareverde.util.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

public class Environment {

    private static final Logger _logger = LoggerFactory.getLogger(Environment.class);

    private static Environment _environment = new Environment();

    public static Environment getInstance() {
        return _environment;
    }

    protected Connection _databaseConnection;

    protected void _initDatabaseConnection() {
        final Configuration configuration = new Configuration(IoUtil.getResource(Resource.serverConfigurationFile));
        final Configuration.DatabaseProperties databaseProperties = configuration.getDatabaseProperties();

        final String url = databaseProperties.getConnectionUrl();
        final Integer port = databaseProperties.getPort();
        final String username = databaseProperties.getUsername();
        final String password = databaseProperties.getPassword();
        final String schema = databaseProperties.getSchema();

        try {
            Class.forName("org.postgresql.Driver");

            final Properties connectionProperties = new Properties();
            connectionProperties.setProperty("user", username);
            connectionProperties.setProperty("password", password);
            _databaseConnection = DriverManager.getConnection("jdbc:postgresql://"+ url +":"+ port +"/"+ schema, connectionProperties);
        }
        catch (final Exception exception) {
            exception.printStackTrace();
            _databaseConnection = null;
        }
    }

    protected Environment() { }

    public Connection getNewDatabaseConnection() {
        if (_databaseConnection == null) {
            _initDatabaseConnection();
        }

        return _databaseConnection;
    }

    public static void close(Connection connection, Statement statement, ResultSet resultSet) {
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
}
