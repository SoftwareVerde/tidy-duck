package com.softwareverde.tidyduck.environment;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseInitializer;
import com.softwareverde.database.jdbc.JdbcDatabase;
import com.softwareverde.database.jdbc.JdbcDatabaseConnection;
import com.softwareverde.database.mysql.MysqlDatabase;
import com.softwareverde.database.mysql.MysqlDatabaseInitializer;
import com.softwareverde.database.mysql.SqlScriptRunner;
import com.softwareverde.database.mysql.embedded.EmbeddedMysqlDatabase;
import com.softwareverde.database.mysql.embedded.properties.MutableEmbeddedDatabaseProperties;
import com.softwareverde.database.properties.MutableDatabaseProperties;
import com.softwareverde.http.server.servlet.routed.Environment;
import com.softwareverde.logging.Logger;
import com.softwareverde.util.IoUtil;
import com.softwareverde.util.Util;


import java.io.File;
import java.io.StringReader;
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
    protected EmbeddedMysqlDatabase _database;

    private final String _cookiesDirectory;
    private final boolean _shouldCreateSecureCookies;
    private final Integer _cookieMaxAgeInSeconds;
    private final boolean _isTwoFactorEnabled;

    protected void _initDatabase() {
        final MysqlDatabaseInitializer databaseInitializer = new MysqlDatabaseInitializer("sql/init.sql", 1, (maintenanceDatabaseConnection, previousVersion, requiredVersion) -> {
            if (previousVersion < requiredVersion) {
                Logger.info(String.format("[Upgrading DB to v%d]", requiredVersion));
                try {
                    final SqlScriptRunner sqlScriptRunner = new SqlScriptRunner(maintenanceDatabaseConnection.getRawConnection(), false, false);
                    sqlScriptRunner.runScript(new StringReader(IoUtil.getResource("/sql/init-accounts.sql")));
                    sqlScriptRunner.runScript(new StringReader(IoUtil.getResource("/sql/load-fake-data.sql")));

                    sqlScriptRunner.runScript(new StringReader(IoUtil.getResource("/sql/migrations/v1.0.0.sql")));
                    sqlScriptRunner.runScript(new StringReader(IoUtil.getResource("/sql/migrations/v1.0.3.sql")));
                    sqlScriptRunner.runScript(new StringReader(IoUtil.getResource("/sql/migrations/v1.0.4.sql")));
                    sqlScriptRunner.runScript(new StringReader(IoUtil.getResource("/sql/migrations/v1.0.5.sql")));
                }
                catch (final Exception exception) {
                    Logger.error(String.format("Unable to upgrade database to v%d", requiredVersion));
                    return false;
                }
            }

            return true;
        });
        final MutableEmbeddedDatabaseProperties databaseProperties = _configuration.getDatabaseProperties();

        _database = new EmbeddedMysqlDatabase(databaseProperties, databaseInitializer);

        try {
            _database.start();
        }
        catch (final Exception exception) {
            throw new RuntimeException("Unable to start database", exception);
        }
    }

    protected TidyDuckEnvironment() {
        _configuration = new Configuration(Resource.serverConfigurationFile);
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
