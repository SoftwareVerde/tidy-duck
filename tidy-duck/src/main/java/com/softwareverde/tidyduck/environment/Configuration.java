package com.softwareverde.tidyduck.environment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    public static class DatabaseProperties {
        private String _connectionUrl;
        private String _username;
        private String _password;
        private String _schema;

        public String getConnectionUrl() { return _connectionUrl; }
        public String getUsername() { return _username; }
        public String getPassword() { return _password; }
        public String getSchema() { return _schema; }
    }

    private final Properties _properties;
    private DatabaseProperties _databaseProperties;

    private void _loadDatabaseProperties() {
        _databaseProperties = new DatabaseProperties();
        _databaseProperties._connectionUrl = _properties.getProperty("database.url", "");
        _databaseProperties._username = _properties.getProperty("database.username", "");
        _databaseProperties._password = _properties.getProperty("database.password", "");
        _databaseProperties._schema = _properties.getProperty("database.schema", "");
    }

    public Configuration(final String configurationFileContents) {
        _properties = new Properties();

        try {
            _properties.load(new ByteArrayInputStream(configurationFileContents.getBytes("UTF-8")));
        }
        catch (final IOException e) { }

        _loadDatabaseProperties();
    }

    public Configuration(final File configurationFile) {
        _properties = new Properties();

        try {
            _properties.load(new FileInputStream(configurationFile));
        }
        catch (final IOException e) { }

        _loadDatabaseProperties();
    }

    public DatabaseProperties getDatabaseProperties() { return _databaseProperties; }
}