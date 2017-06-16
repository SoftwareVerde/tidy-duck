package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.tidyduck.FunctionBlock;
import com.softwareverde.tidyduck.FunctionCatalog;
import com.softwareverde.database.transaction.DatabaseConnectedRunnable;
import com.softwareverde.database.transaction.JdbcDatabaseTransaction;
import com.softwareverde.tidyduck.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class DatabaseManager {

    private final Logger _logger = LoggerFactory.getLogger(getClass());
    private final Environment _environment;

    public DatabaseManager(Environment environment) {
        _environment = environment;
    }

    protected void executeTransaction(DatabaseConnectedRunnable<Connection> databaseConnectedRunnable) throws DatabaseException {
        final JdbcDatabaseTransaction jdbcDatabaseTransaction = new JdbcDatabaseTransaction(_environment);
        jdbcDatabaseTransaction.execute(databaseConnectedRunnable);
    }

    // FUNCTION CATALOG METHODS

    public void insertFunctionCatalog(final long versionId, final FunctionCatalog functionCatalog) throws DatabaseException {
        this.executeTransaction(new DatabaseConnectedRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                functionCatalogDatabaseManager.insertFunctionCatalogForVersion(versionId, functionCatalog);
            }
        });
    }

    public void updateFunctionCatalog(final long versionId, final FunctionCatalog functionCatalog) throws DatabaseException {
        this.executeTransaction(new DatabaseConnectedRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                functionCatalogDatabaseManager.updateFunctionCatalogForVersion(versionId, functionCatalog);
            }
        });
    }

    public void deleteFunctionCatalog(final long versionId, final long functionCatalogId) throws DatabaseException {
        this.executeTransaction(new DatabaseConnectedRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                functionCatalogDatabaseManager.deleteFunctionCatalogFromVersion(versionId, functionCatalogId);
            }
        });
    }

    // FUNCTION BLOCK METHODS

    public void insertFunctionBlock(final Long functionCatalogId, final FunctionBlock functionBlock) throws DatabaseException {
        this.executeTransaction(new DatabaseConnectedRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.insertFunctionBlockForFunctionCatalog(functionCatalogId, functionBlock);
            }
        });
    }
}
