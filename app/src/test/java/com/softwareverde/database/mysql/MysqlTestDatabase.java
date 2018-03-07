package com.softwareverde.database.mysql;

import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import com.softwareverde.database.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;

public class MysqlTestDatabase extends MysqlDatabase {
    protected final DB _databaseInstance;
    protected final DatabaseConnectionFactory _databaseConnectionFactory;
    protected final Credentials _credentials;

    protected final String _rootUsername = "root";
    protected final String _rootPassword = "";
    protected final String _databaseSchema = "tidy_duck";

    @Override
    protected Connection _connect() throws SQLException, ClassNotFoundException {
        throw new RuntimeException("MysqlTestDatabase._connect() is not supported.");
    }

    public MysqlTestDatabase() {
        super(null, null, null);
        final DBConfiguration dbConfiguration;
        {
            final DBConfigurationBuilder configBuilder = DBConfigurationBuilder.newBuilder();
            dbConfiguration = configBuilder.build();

            final String connectionString = configBuilder.getURL(_databaseSchema);
            _databaseConnectionFactory = new DatabaseConnectionFactory(connectionString, _rootUsername, _rootPassword);
        }

        {
            try {
                _databaseInstance = DB.newEmbeddedDB(dbConfiguration);
                _databaseInstance.start();
                _databaseInstance.createDB(_databaseSchema);
            }
            catch (final Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        _credentials = new Credentials(_rootUsername, _rootPassword, _databaseSchema);
    }

    @Override
    public void setDatabase(final String databaseName) throws DatabaseException {
        throw new DatabaseException("MysqlTestDatabase.setDatabase() is not supported.");
    }

    @Override
    public MysqlDatabaseConnection newConnection() throws DatabaseException {
        try {
            return _databaseConnectionFactory.newConnection();
        }
        catch (final Exception exception) {
            throw new DatabaseException("Unable to connect to database.", exception);
        }
    }

    public void reset() throws DatabaseException {
        try {
            _databaseInstance.run("DROP DATABASE " + _databaseSchema, _rootUsername, _rootPassword);
            _databaseInstance.createDB(_databaseSchema);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public DB getDatabaseInstance() {
        return _databaseInstance;
    }

    public Credentials getCredentials() {
        return _credentials;
    }

    public DatabaseConnectionFactory getDatabaseConnectionFactory() {
        return _databaseConnectionFactory;
    }
}