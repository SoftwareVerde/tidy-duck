package com.softwareverde.tidyduck.database;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.transaction.DatabaseRunnable;
import com.softwareverde.database.transaction.JdbcDatabaseTransaction;
import com.softwareverde.tidyduck.Settings;
import com.softwareverde.tidyduck.most.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.util.List;

public class DatabaseManager {

    private final Logger _logger = LoggerFactory.getLogger(getClass());
    private final Database<Connection> _database;

    public DatabaseManager(final Database database) {
        _database = database;
    }

    protected void executeTransaction(final DatabaseRunnable<Connection> databaseRunnable) throws DatabaseException {
        final JdbcDatabaseTransaction jdbcDatabaseTransaction = new JdbcDatabaseTransaction(_database);
        jdbcDatabaseTransaction.execute(databaseRunnable);
    }

    // ACCOUNT METHODS

    public void updateAccountSettings(final long accountId, final Settings settings) throws DatabaseException {
        try (DatabaseConnection<Connection> databaseConnection = _database.newConnection()) {
            final AccountDatabaseManager accountDatabaseManager = new AccountDatabaseManager(databaseConnection);
            accountDatabaseManager.updateAccountSettings(accountId, settings);
        }
    }

    // FUNCTION CATALOG METHODS

    public void insertFunctionCatalog(final FunctionCatalog functionCatalog) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                functionCatalogDatabaseManager.insertFunctionCatalog(functionCatalog);
            }
        });
    }

    public void updateFunctionCatalog(final FunctionCatalog functionCatalog) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                functionCatalogDatabaseManager.updateFunctionCatalog(functionCatalog);
            }
        });
    }

    public void deleteFunctionCatalog(final long functionCatalogId) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                functionCatalogDatabaseManager.deleteFunctionCatalog(functionCatalogId);
            }
        });
    }

    public void releaseFunctionCatalog(final long functionCatalogId) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                functionCatalogDatabaseManager.releaseFunctionCatalog(functionCatalogId);
            }
        });
    }
    // FUNCTION BLOCK METHODS

    public void insertFunctionBlock(final Long functionCatalogId, final FunctionBlock functionBlock) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.insertFunctionBlockForFunctionCatalog(functionCatalogId, functionBlock);
            }
        });
    }

    public void insertOrphanedFunctionBlock(final FunctionBlock functionBlock) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.insertOrphanedFunctionBlock(functionBlock);
            }
        });
    }

    public void associateFunctionBlockWithFunctionCatalog(final long functionCatalogId, final long functionBlockId) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.associateFunctionBlockWithFunctionCatalog(functionCatalogId, functionBlockId);
            }
        });
    }

    public void updateFunctionBlock(final long functionCatalogId, final FunctionBlock functionBlock) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.updateFunctionBlockForFunctionCatalog(functionCatalogId, functionBlock);
            }
        });
    }

    public void deleteFunctionBlock(final long functionCatalogId, final long functionBlockId) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.deleteFunctionBlockFromFunctionCatalog(functionCatalogId, functionBlockId);
            }
        });
    }

    public List<Long> listFunctionCatalogsContainingFunctionBlock(final long functionBlockId) throws DatabaseException {
        try (final DatabaseConnection<Connection> databaseConnection = _database.newConnection()) {
            final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
            return functionBlockDatabaseManager.listFunctionCatalogIdsContainingFunctionBlock(functionBlockId);
        }
    }

    // MOST INTERFACE METHODS

    public void insertMostInterface(final Long functionBlockId, final MostInterface mostInterface) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.insertMostInterfaceForFunctionBlock(functionBlockId, mostInterface);

            }
        });
    }

    public void insertOrphanedMostInterface(final MostInterface mostInterface) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.insertOrphanedMostInterface(mostInterface);
            }
        });
    }

    public void associateMostInterfaceWithFunctionBlock(final long functionBlockId, final long mostInterfaceId) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.associateMostInterfaceWithFunctionBlock(functionBlockId, mostInterfaceId);
            }
        });
    }

    public void updateMostInterface(final long functionBlockId, final MostInterface mostInterface) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.updateMostInterfaceForFunctionBlock(functionBlockId, mostInterface);
            }
        });
    }

    public void deleteMostInterface(final long functionBlockId, final long mostInterfaceId) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.deleteMostInterfaceFromFunctionBlock(functionBlockId, mostInterfaceId);
            }
        });
    }

    public List<Long> listFunctionBlocksContainingMostInterface(final long mostInterfaceId) throws DatabaseException {
        try (DatabaseConnection<Connection> databaseConnection = _database.newConnection()) {
            final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
            return mostInterfaceDatabaseManager.listFunctionBlocksContainingMostInterface(mostInterfaceId);
        }
    }

    // MOST FUNCTION METHODS

    public void insertMostFunction(final Long mostInterfaceId, final MostFunction mostFunction) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                MostFunctionDatabaseManager mostFunctionDatabaseManager = new MostFunctionDatabaseManager(databaseConnection);
                mostFunctionDatabaseManager.insertMostFunctionForMostInterface(mostInterfaceId, mostFunction);
            }
        });
    }

    public void updateMostFunction(final long mostInterfaceId, final MostFunction mostFunction) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostFunctionDatabaseManager mostFunctionDatabaseManager = new MostFunctionDatabaseManager(databaseConnection);
                mostFunctionDatabaseManager.updateMostFunctionForMostInterface(mostInterfaceId, mostFunction);
            }
        });
    }

    public void deleteMostFunction(final long mostInterfaceId, final long mostFunctionId) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostFunctionDatabaseManager mostFunctionDatabaseManager = new MostFunctionDatabaseManager(databaseConnection);
                mostFunctionDatabaseManager.deleteMostFunctionFromMostInterface(mostInterfaceId, mostFunctionId);
            }
        });
    }

    // MOST TYPE METHODS

    public void insertMostType(final MostType mostType) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostTypeDatabaseManager mostTypeDatabaseManager = new MostTypeDatabaseManager(databaseConnection);
                mostTypeDatabaseManager.insertMostType(mostType);
            }
        });
    }

    public void updateMostType(final MostType mostType) throws DatabaseException {
        this.executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostTypeDatabaseManager mostTypeDatabaseManager = new MostTypeDatabaseManager(databaseConnection);
                mostTypeDatabaseManager.updateMostType(mostType);
            }
        });
    }
}
