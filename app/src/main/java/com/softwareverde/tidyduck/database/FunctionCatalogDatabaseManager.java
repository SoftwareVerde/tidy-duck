package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.FunctionCatalog;

class FunctionCatalogDatabaseManager {

    private final DatabaseConnection _databaseConnection;

    public FunctionCatalogDatabaseManager(final DatabaseConnection databaseConnection) {
        _databaseConnection = databaseConnection;
    }    

    public void insertFunctionCatalogForVersion(final long versionId, final FunctionCatalog functionCatalog) throws DatabaseException {
        _insertFunctionCatalog(functionCatalog);
        _associateFunctionCatalogWithVersion(versionId, functionCatalog.getId());
    }

    /**
     * Stores the functionCatalog's release, releaseDate, accountId, and companyId via the databaseConnection.
     * Upon successful insert, the functionCatalog's Id is set to the database's insertId.
     */
    private void _insertFunctionCatalog(final FunctionCatalog functionCatalog) throws DatabaseException {
        final String name = functionCatalog.getName();
        final String release = functionCatalog.getRelease();
        final String releaseDate = DateUtil.timestampToDateString(functionCatalog.getReleaseDate().getTime());
        final Long accountId = functionCatalog.getAuthor().getId();
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

    private long _associateFunctionCatalogWithVersion(final long versionId, final long functionCatalogId) throws DatabaseException {
        final Query query = new Query("INSERT INTO versions_function_catalogs (version_id, function_catalog_id) VALUES (?, ?)")
            .setParameter(versionId)
            .setParameter(functionCatalogId)
        ;

        return _databaseConnection.executeSql(query);
    }

    public void updateFunctionCatalogForVersion(final long versionId, final FunctionCatalog proposedFunctionCatalog) throws DatabaseException {
        final long inputFunctionCatalogId = proposedFunctionCatalog.getId();

        MostCatalogInflater mostCatalogInflater = new MostCatalogInflater(_databaseConnection);
        FunctionCatalog databaseFunctionCatalog = mostCatalogInflater.inflateFunctionCatalog(inputFunctionCatalogId);
        if (!databaseFunctionCatalog.isCommitted()) {
            // not committed, can update existing function catalog
            _updateUncommittedFunctionCatalog(proposedFunctionCatalog);
        } else {
            // current catalog is committed to a version
            // need to insert a new function catalog replace this one
            _insertFunctionCatalog(proposedFunctionCatalog);
            final long newFunctionCatalogId = proposedFunctionCatalog.getId();
            // change association with version
            _disassociateFunctionCatalogWithVersion(versionId, inputFunctionCatalogId);
            _associateFunctionCatalogWithVersion(versionId, newFunctionCatalogId);
        }
    }

    private void _updateUncommittedFunctionCatalog(FunctionCatalog proposedFunctionCatalog) throws DatabaseException {
        final String newName = proposedFunctionCatalog.getName();
        final String newReleaseVersion = proposedFunctionCatalog.getRelease();
        final String newReleaseDate = DateUtil.dateToDateString(proposedFunctionCatalog.getReleaseDate());
        final long newAuthorId = proposedFunctionCatalog.getAuthor().getId();
        final long newCompanyId = proposedFunctionCatalog.getCompany().getId();
        final long functionCatalogId = proposedFunctionCatalog.getId();

        final Query query = new Query("UPDATE function_catalogs SET name = ?, release_version = ?, release_date = ?, account_id = ?, company_id = ? WHERE id = ?")
            .setParameter(newName)
            .setParameter(newReleaseVersion)
            .setParameter(newReleaseDate)
            .setParameter(newAuthorId)
            .setParameter(newCompanyId)
            .setParameter(functionCatalogId)
        ;

        _databaseConnection.executeSql(query);
    }

    public void deleteFunctionCatalogFromVersion(final long versionId, final long functionCatalogId) throws DatabaseException {
        _disassociateFunctionCatalogWithVersion(versionId, functionCatalogId);
        _deleteFunctionCatalogIfUncommitted(functionCatalogId);
    }

    private void _disassociateFunctionCatalogWithVersion(final long versionId, final long functionCatalogId) throws DatabaseException {
        final Query query = new Query("DELETE FROM versions_function_catalogs WHERE version_id = ? and function_catalog_id = ?")
            .setParameter(versionId)
            .setParameter(functionCatalogId)
        ;

        _databaseConnection.executeSql(query);
    }

    private void _deleteFunctionCatalogIfUncommitted(long functionCatalogId) throws DatabaseException {
        MostCatalogInflater mostCatalogInflater = new MostCatalogInflater(_databaseConnection);
        FunctionCatalog functionCatalog = mostCatalogInflater.inflateFunctionCatalog(functionCatalogId);

        if (!functionCatalog.isCommitted()) {
            // function catalog isn't committed, we can delete it
            final Query query = new Query("DELETE FROM function_catalogs WHERE id = ?")
                .setParameter(functionCatalogId)
            ;

            _databaseConnection.executeSql(query);

            // TODO: delete any uncommitted function blocks
        }
    }
}
