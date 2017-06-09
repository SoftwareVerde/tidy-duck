package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.tidyduck.FunctionCatalog;
import com.softwareverde.tidyduck.environment.Environment;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private final Environment _environment;

    public DatabaseManager(Environment environment) {
        _environment = environment;
    }

    public void insertFunctionCatalog(final FunctionCatalog functionCatalog, final long versionId) throws DatabaseException {
        DatabaseConnection<Connection> databaseConnection = null;
        Connection connection = null;
        try {
            databaseConnection = _environment.getNewDatabaseConnection();
            connection = databaseConnection.getRawConnection();
            connection.setAutoCommit(false);

            FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
            functionCatalogDatabaseManager.insertFunctionCatalog(functionCatalog);
            functionCatalogDatabaseManager.associateFunctionCatalogWithVersion(versionId, functionCatalog.getId());
            connection.commit();
        } catch (Exception e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                throw new DatabaseException("Unable rollback connection.", e1);
            }
        } finally {
            Environment.close(databaseConnection);
        }
    }
}
