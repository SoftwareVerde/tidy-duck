package com.softwareverde.tidyduck.environment;

import com.softwareverde.util.Util;

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
        private Integer _port;

        public String getConnectionUrl() { return _connectionUrl; }
        public String getUsername() { return _username; }
        public String getPassword() { return _password; }
        public String getSchema() { return _schema; }
        public Integer getPort() { return _port; }
    }

    public static class ServerProperties {
        private Integer _port;
        private Integer _tlsPort;
        private Integer _externalTlsPort;
        private String _tlsKeyFile;
        private String _tlsCertificateFile;
        private String _localEncryptionKeyPath;

        public Integer getPort() { return _port; }
        public Integer getTlsPort() { return _tlsPort; }
        public String getTlsKeyFile() { return _tlsKeyFile; }
        public String getTlsCertificateFile() { return _tlsCertificateFile; }
        public Integer getExternalTlsPort() { return _externalTlsPort; }
        public String getLocalEncryptionKeyPath() { return _localEncryptionKeyPath; }

        public Boolean hasTlsEnabled() {
            return ( (_tlsPort > 0) && (_tlsKeyFile != null) && (_tlsCertificateFile != null) );
        }
    }

    private final Properties _properties;
    private DatabaseProperties _databaseProperties;
    private ServerProperties _serverProperties;

    private void _loadDatabaseProperties() {
        _databaseProperties = new DatabaseProperties();
        _databaseProperties._connectionUrl = _properties.getProperty("database.url", "");
        _databaseProperties._username = _properties.getProperty("database.username", "");
        _databaseProperties._password = _properties.getProperty("database.password", "");
        _databaseProperties._schema = _properties.getProperty("database.schema", "");
        _databaseProperties._port = Util.parseInt(_properties.getProperty("database.port", ""));
    }

    private void _loadServerProperties() {
        final Integer port = Util.parseInt(_properties.getProperty("server.httpPort", "8080"));

        final Integer tlsPort = Util.parseInt(_properties.getProperty("server.tlsPort", "4443"));
        final Integer externalTlsPort = Util.parseInt(_properties.getProperty("server.externalTlsPort", "4443"));
        final String tlsKeyFile = _properties.getProperty("server.tlsKeyFile", "");
        final String tlsCertificateFile = _properties.getProperty("server.tlsCertificateFile", "");
        final String localEncryptionKeyPath = _properties.getProperty("server.localEncryptionKeyPath", "");

        final ServerProperties serverProperties = new ServerProperties();
        serverProperties._port = port;

        serverProperties._tlsPort = tlsPort;
        serverProperties._externalTlsPort = externalTlsPort;
        serverProperties._tlsKeyFile = (tlsKeyFile.isEmpty() ? null : tlsKeyFile);
        serverProperties._tlsCertificateFile = (tlsCertificateFile.isEmpty() ? null : tlsCertificateFile);
        serverProperties._localEncryptionKeyPath = localEncryptionKeyPath;

        _serverProperties = serverProperties;
    }

    public Configuration(final String configurationFileContents) {
        _properties = new Properties();

        try {
            _properties.load(new ByteArrayInputStream(configurationFileContents.getBytes("UTF-8")));
        }
        catch (final IOException e) { }

        _loadDatabaseProperties();
        _loadServerProperties();
    }

    public Configuration(final File configurationFile) {
        _properties = new Properties();

        try {
            _properties.load(new FileInputStream(configurationFile));
        }
        catch (final IOException e) { }

        _loadDatabaseProperties();
        _loadServerProperties();
    }

    public DatabaseProperties getDatabaseProperties() { return _databaseProperties; }

    public ServerProperties getServerProperties() { return _serverProperties; }

    public String getProperty(final String key) {
        return _properties.getProperty(key);
    }

    public String getProperty(final String key, final String defaultValue) {
        return _properties.getProperty(key, defaultValue);
    }
}
