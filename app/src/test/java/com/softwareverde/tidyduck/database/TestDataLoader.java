package com.softwareverde.tidyduck.database;


import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.util.IoUtil;

import java.sql.Connection;

public class TestDataLoader {
    public static int generateRandomAutoIncrementId() {
        // generate random ID but make sure it's greater than zero
        return (int) (((Math.random() * 7777) % 1000) + 1);
    }

    public static void initDatabase(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
        databaseConnection.executeSql(new Query(IoUtil.getResource("/sql/init.sql")));
        databaseConnection.executeSql(new Query(IoUtil.getResource("/sql/migrations/v1.0.0.sql")));
        databaseConnection.executeSql(new Query(IoUtil.getResource("/sql/migrations/v1.0.3.sql")));
    }

    public static void insertFakeCompany(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
        databaseConnection.executeSql(new Query("INSERT INTO companies (name) VALUES ('Software Verde, LLC')"));
    }

    public static void insertFakeAccount(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
        databaseConnection.executeSql(new Query("INSERT INTO accounts (name, company_id) VALUES ('Josh Green', 1)"));
        databaseConnection.executeSql(new Query("INSERT INTO accounts_roles VALUES (1, 1), (1, 2), (1, 3), (1, 4), (1, 5)"));
    }

    public static void insertFakeCompany(final DatabaseConnection<Connection> databaseConnection, final String companyName) throws DatabaseException {
        databaseConnection.executeSql(new Query("INSERT INTO companies (name) VALUES (?)").setParameter(companyName));
    }

    public static void insertFakeAccount(final DatabaseConnection<Connection> databaseConnection, final String accountName, final Long companyId) throws DatabaseException {
        databaseConnection.executeSql(new Query("INSERT INTO accounts (name, company_id) VALUES (?, ?)").setParameter(accountName).setParameter(companyId));
    }

    /**
     * <p>Inserts a review for a single component.</p>
     *
     * <p>If functionCatalogId is not null, only a review for that will be added.  Then functionBlockId will be checked, then interfaceId.</p>
     * @param databaseConnection
     * @param functionCatalogId
     * @param functionBlockId
     * @param interfaceId
     */
    public static Long insertFakeReview(final DatabaseConnection<Connection> databaseConnection, final Long functionCatalogId, final Long functionBlockId, final Long interfaceId, final long accountId) throws DatabaseException {
        if (functionCatalogId != null) {
            return databaseConnection.executeSql(new Query("INSERT INTO reviews (function_catalog_id, account_id, created_date) VALUES (?, ?, NOW())").setParameter(functionCatalogId).setParameter(accountId));

        }
        if (functionBlockId != null) {
            return databaseConnection.executeSql(new Query("INSERT INTO reviews (function_block_id, account_id, created_date) VALUES (?, ?, NOW())").setParameter(functionBlockId).setParameter(accountId));
        }
        if (interfaceId != null) {
            return databaseConnection.executeSql(new Query("INSERT INTO reviews (interface_id, account_id, created_date) VALUES (?, ?, NOW())").setParameter(interfaceId).setParameter(accountId));
        }
        return null;
    }

    public static long insertFakeCompleteFunctionCatalog(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
        final long functionCatalogId = databaseConnection.executeSql(new Query("INSERT INTO function_catalogs (name, release_version, account_id, company_id, base_version_id) VALUES (?, ?, ?, ?, ?)")
                .setParameter("TestFcat")
                .setParameter("1.0")
                .setParameter(1)
                .setParameter(1)
                .setParameter(1)
        );

        final long functionBlockId = insertFakeFunctionBlock(databaseConnection);
        databaseConnection.executeSql(new Query("INSERT INTO function_catalogs_function_blocks (function_catalog_id, function_block_id) VALUES (?, ?)")
                .setParameter(functionCatalogId)
                .setParameter(functionBlockId)
        );

        return functionCatalogId;
    }

    private static long insertFakeFunctionBlock(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
        final long functionBlockId = databaseConnection.executeSql(new Query("INSERT INTO function_blocks (most_id, kind, name, description, last_modified_date, release_version, account_id, company_id, access, base_version_id) VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?)")
                .setParameter("0xFF")
                .setParameter("Proprietary")
                .setParameter("TestFblo")
                .setParameter("Description.")
                .setParameter("1.0")
                .setParameter(1)
                .setParameter(1)
                .setParameter("Private")
                .setParameter(1)
        );

        final long mostInterfaceId = insertFakeMostInterface(databaseConnection);
        databaseConnection.executeSql(new Query("INSERT INTO function_blocks_interfaces (function_block_id, interface_id) VALUES (?, ?)")
                .setParameter(functionBlockId)
                .setParameter(mostInterfaceId)
        );

        return functionBlockId;
    }

    private static long insertFakeMostInterface(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
        final long mostInterfaceId = databaseConnection.executeSql(new Query("INSERT INTO interfaces (most_id, name, description, last_modified_date, version, base_version_id) VALUES (?, ?, ?, NOW(), ?, ?)")
                .setParameter("1")
                .setParameter("TestInterface")
                .setParameter("Description.")
                .setParameter("1")
                .setParameter(1)
        );

        return mostInterfaceId;
    }

    /// TODO: Insert Function and Return Type for Function
    /*
    private static long insertFakeMostFunction(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {

    }

    private static long insertFakeMostType(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
    */
}
