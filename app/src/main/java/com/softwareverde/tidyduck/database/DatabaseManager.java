package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.transaction.DatabaseConnectedRunnable;
import com.softwareverde.database.transaction.JdbcDatabaseTransaction;
import com.softwareverde.tidyduck.FunctionBlock;
import com.softwareverde.tidyduck.FunctionCatalog;
import com.softwareverde.tidyduck.MostInterface;
import com.softwareverde.tidyduck.Settings;
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

    // ACCOUNT METHODS
    public void updateAccountSettings(final long accountId, final Settings settings) throws DatabaseException {
        try (DatabaseConnection<Connection> databaseConnection = _environment.getNewDatabaseConnection()) {
            AccountDatabaseManager accountDatabaseManager = new AccountDatabaseManager(databaseConnection);
            accountDatabaseManager.updateAccountSettings(accountId, settings);
        }
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

    public void associateFunctionBlockWithFunctionCatalog(final long functionCatalogId, final long functionBlockId) throws DatabaseException {
        this.executeTransaction(new DatabaseConnectedRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.associateFunctionBlockWithFunctionCatalog(functionCatalogId, functionBlockId);
            }
        });
    }

    public void updateFunctionBlock(final long functionCatalogId, final FunctionBlock functionBlock) throws DatabaseException {
        this.executeTransaction(new DatabaseConnectedRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.updateFunctionBlockForFunctionCatalog(functionCatalogId, functionBlock);
            }
        });
    }

    public void deleteFunctionBlock(final long functionCatalogId, final long functionBlockId) throws DatabaseException {
        this.executeTransaction(new DatabaseConnectedRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.deleteFunctionBlockFromFunctionCatalog(functionCatalogId, functionBlockId);
            }
        });
    }

    //MOST INTERFACE METHODS

    public void insertMostInterface(final Long functionBlockId, final MostInterface mostInterface) throws DatabaseException {
        this.executeTransaction(new DatabaseConnectedRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.insertMostInterfaceForFunctionBlock(functionBlockId, mostInterface);
            }
        });
    }

    public void associateMostInterfaceWithFunctionBlock(final long functionBlockId, final long mostInterfaceId) throws DatabaseException {
        this.executeTransaction(new DatabaseConnectedRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.associateMostInterfaceWithFunctionBlock(functionBlockId, mostInterfaceId);
            }
        });
    }

    public void updateMostInterface(final long functionBlockId, final MostInterface mostInterface) throws DatabaseException {
        this.executeTransaction(new DatabaseConnectedRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.updateMostInterfaceForFunctionBlock(functionBlockId, mostInterface);
            }
        });
    }

    public void deleteMostInterface(final long functionBlockId, final long mostInterfaceId) throws DatabaseException {
        this.executeTransaction(new DatabaseConnectedRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.deleteMostInterfaceFromFunctionBlock(functionBlockId, mostInterfaceId);
            }
        });
    }
}
