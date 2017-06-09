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

    protected Configuration.DatabaseProperties _databaseProperties;

    protected void _initDatabaseProperties() {
        final Configuration configuration = new Configuration(IoUtil.getResource(Resource.serverConfigurationFile));
        _databaseProperties = configuration.getDatabaseProperties();
    }

    protected Environment() { }

    public Connection getNewDatabaseConnection() throws SQLException {
        if (_databaseProperties == null) {
            _initDatabaseProperties();
        }

        final String url = _databaseProperties.getConnectionUrl();
        final Integer port = _databaseProperties.getPort();
        final String username = _databaseProperties.getUsername();
        final String password = _databaseProperties.getPassword();
        final String schema = _databaseProperties.getSchema();

        try {
            Class.forName("org.postgresql.Driver");
        }  catch (final ClassNotFoundException exception) {
            throw new SQLException("Unable to locate driver.", exception);
        }

        final Properties connectionProperties = new Properties();
        connectionProperties.setProperty("user", username);
        connectionProperties.setProperty("password", password);
        return DriverManager.getConnection("jdbc:postgresql://"+ url +":"+ port +"/"+ schema, connectionProperties);
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
