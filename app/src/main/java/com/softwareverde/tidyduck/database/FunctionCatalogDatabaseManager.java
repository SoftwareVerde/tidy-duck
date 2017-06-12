package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.FunctionCatalog;

class FunctionCatalogDatabaseManager {

    private final DatabaseConnection _databaseConnection;

    public FunctionCatalogDatabaseManager(DatabaseConnection databaseConnection) {
        _databaseConnection = databaseConnection;
    }    

    /**
     * Stores the functionCatalog's release, releaseDate, accountId, and companyId via the databaseConnection.
     * Upon successful insert, the functionCatalog's Id is set to the database's insertId.
     */
    public void insertFunctionCatalog(final FunctionCatalog functionCatalog) throws DatabaseException {
        final String name = functionCatalog.getName();
        final String release = functionCatalog.getRelease();
        final String releaseDate = DateUtil.timestampToDatetimeString(functionCatalog.getReleaseDate().getTime());
        final Long accountId = functionCatalog.getAccount().getId();
        final Long companyId = functionCatalog.getCompany().getId();

        final Query query = new Query("INSERT INTO function_catalogs (name, release_version, release_date, account_id, company_id) VALUES (?, ?, ?, ?, ?)")
            .setParameter(name)
            .setParameter(release)
            .setParameter(releaseDate)
            .setParameter(accountId)
            .setParameter(companyId)
        ;

        final long functionCatalogId = _databaseConnection.executeSql(query);
        functionCatalog.setId(functionCatalogId);
    }

    public long associateFunctionCatalogWithVersion(final long versionId, final long functionCatalogId) throws DatabaseException {
        final Query query = new Query("INSERT INTO versions_function_catalogs (version_id, function_catalog_id) VALUES (?, ?)")
            .setParameter(versionId)
            .setParameter(functionCatalogId)
        ;

        return _databaseConnection.executeSql(query);
    }
}
