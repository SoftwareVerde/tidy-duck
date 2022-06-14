package com.softwareverde.tidyduck.environment;

import com.softwareverde.database.jdbc.JdbcDatabase;
import com.softwareverde.database.mysql.MysqlDatabase;
import com.softwareverde.database.properties.MutableDatabaseProperties;
import com.softwareverde.http.server.servlet.routed.Environment;
import com.softwareverde.util.IoUtil;
import com.softwareverde.util.Util;


import java.sql.*;

public class TidyDuckEnvironment implements Environment {
    private static final String SHOULD_CREATE_SECURE_COOKIES_PROPERTY = "server.shouldCreateSecureCookies";
    private static final String COOKIES_DIRECTORY_PROPERTY = "server.cookiesDirectory";
    private static final String COOKIES_MAX_AGE_PROPERTY = "server.cookieMaxAge";
    private static final String DEFAULT_COOKIES_DIRECTORY_PATH = "cookies/";

    private static final String ENABLE_TWO_FACTOR_PROPERTY = "server.enableTwoFactor";

    private static TidyDuckEnvironment _environment;
    public static TidyDuckEnvironment getInstance() {
        if (_environment == null) {
             _environment = new TidyDuckEnvironment();
        }

        return _environment;
    }

    private final Configuration _configuration;
    protected JdbcDatabase _database;

    private final String _cookiesDirectory;
    private final boolean _shouldCreateSecureCookies;
    private final Integer _cookieMaxAgeInSeconds;
    private final boolean _isTwoFactorEnabled;

    protected void _initDatabase() {
        final Configuration.DatabaseProperties configurationDatabaseProperties = _configuration.getDatabaseProperties();

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

        _database = new MysqlDatabase(databaseProperties);
    }

    protected TidyDuckEnvironment() {
        _configuration = new Configuration(IoUtil.getResource(Resource.serverConfigurationFile));
        _cookiesDirectory = _configuration.getProperty(COOKIES_DIRECTORY_PROPERTY, DEFAULT_COOKIES_DIRECTORY_PATH);

        _shouldCreateSecureCookies = Boolean.parseBoolean(_configuration.getProperty(SHOULD_CREATE_SECURE_COOKIES_PROPERTY, "true"));
        _cookieMaxAgeInSeconds = Util.parseInt(_configuration.getProperty(COOKIES_MAX_AGE_PROPERTY), null);

        _isTwoFactorEnabled = Boolean.parseBoolean(_configuration.getProperty(ENABLE_TWO_FACTOR_PROPERTY, "true"));

        _initDatabase();
    }

    public JdbcDatabase getDatabase() {
        return _database;
    }

    public Configuration getConfiguration() { return _configuration; }

    public String getCookiesDirectory() {
        return _cookiesDirectory;
    }

    public Integer getCookieMaxAgeInSeconds() {
        return _cookieMaxAgeInSeconds;
    }

    public boolean shouldCreateSecureCookies() { return _shouldCreateSecureCookies; }

    public boolean isTwoFactorEnabled() {
        return _isTwoFactorEnabled;
    }
}
