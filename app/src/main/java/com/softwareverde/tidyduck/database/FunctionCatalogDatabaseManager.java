package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.logging.Logger;
import com.softwareverde.logging.slf4j.Slf4jLogger;
import com.softwareverde.tidyduck.FunctionBlock;
import com.softwareverde.tidyduck.FunctionCatalog;

import java.sql.Connection;
import java.util.List;

class FunctionCatalogDatabaseManager {
    private final Logger _logger = new Slf4jLogger(this.getClass());
    private final DatabaseConnection<Connection> _databaseConnection;

    /**
     * Stores the functionCatalog's release, releaseDate, accountId, and companyId via the databaseConnection.
     * Upon successful insert, the functionCatalog's Id is set to the database's insertId.
     */
    private void _insertFunctionCatalog(final FunctionCatalog functionCatalog) throws DatabaseException {
        final String name = functionCatalog.getName();
        final String release = functionCatalog.getRelease();
        final Long accountId = functionCatalog.getAuthor().getId();
        final Long companyId = functionCatalog.getCompany().getId();

        final Query query = new Query("INSERT INTO function_catalogs (name, release_version, account_id, company_id) VALUES (?, ?, ?, ?)")
            .setParameter(name)
            .setParameter(release)
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

    private void _updateUncommittedFunctionCatalog(FunctionCatalog proposedFunctionCatalog) throws DatabaseException {
        final String newName = proposedFunctionCatalog.getName();
        final String newReleaseVersion = proposedFunctionCatalog.getRelease();
        final long newAuthorId = proposedFunctionCatalog.getAuthor().getId();
        final long newCompanyId = proposedFunctionCatalog.getCompany().getId();
        final long functionCatalogId = proposedFunctionCatalog.getId();

        final Query query = new Query("UPDATE function_catalogs SET name = ?, release_version = ?, account_id = ?, company_id = ? WHERE id = ?")
            .setParameter(newName)
            .setParameter(newReleaseVersion)
            .setParameter(newAuthorId)
            .setParameter(newCompanyId)
            .setParameter(functionCatalogId)
        ;

        _databaseConnection.executeSql(query);
    }

    private void _disassociateFunctionCatalogWithVersion(final long versionId, final long functionCatalogId) throws DatabaseException {
        final Query query = new Query("DELETE FROM versions_function_catalogs WHERE version_id = ? and function_catalog_id = ?")
            .setParameter(versionId)
            .setParameter(functionCatalogId)
        ;

        _databaseConnection.executeSql(query);
    }

    private void _deleteFunctionCatalogIfUncommitted(final long functionCatalogId) throws DatabaseException {
        final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(_databaseConnection);
        final FunctionCatalog functionCatalog = functionCatalogInflater.inflateFunctionCatalog(functionCatalogId);

        if (! functionCatalog.isCommitted()) {
            // function catalog isn't committed, we can delete it
            _deleteFunctionBlocksFromFunctionCatalog(functionCatalogId);
            _deleteFunctionCatalogFromDatabase(functionCatalogId);
        }
    }

    private void _deleteFunctionBlocksFromFunctionCatalog(final long functionCatalogId) throws DatabaseException {
        final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
        final List<FunctionBlock> functionBlocks = functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalogId);

        final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(_databaseConnection);
        for (final FunctionBlock functionBlock : functionBlocks) {
            functionBlockDatabaseManager.deleteFunctionBlockFromFunctionCatalog(functionCatalogId, functionBlock.getId());
        }
    }

    private void _deleteFunctionCatalogFromDatabase(final long functionCatalogId) throws DatabaseException {
        final Query query = new Query("DELETE FROM function_catalogs WHERE id = ?")
            .setParameter(functionCatalogId)
        ;

        _databaseConnection.executeSql(query);
    }

    public FunctionCatalogDatabaseManager(final DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public void insertFunctionCatalogForVersion(final long versionId, final FunctionCatalog functionCatalog) throws DatabaseException {
        _insertFunctionCatalog(functionCatalog);
        _associateFunctionCatalogWithVersion(versionId, functionCatalog.getId());
    }

    public void updateFunctionCatalogForVersion(final long versionId, final FunctionCatalog functionCatalog) throws DatabaseException {
        final long inputFunctionCatalogId = functionCatalog.getId();

        final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(_databaseConnection);
        final FunctionCatalog databaseFunctionCatalog = functionCatalogInflater.inflateFunctionCatalog(inputFunctionCatalogId);
        if (databaseFunctionCatalog.isCommitted()) {
            // need to insert a new function catalog replace this one
            _insertFunctionCatalog(functionCatalog);
            final long newFunctionCatalogId = functionCatalog.getId();

            // change association with version
            _disassociateFunctionCatalogWithVersion(versionId, inputFunctionCatalogId);
            _associateFunctionCatalogWithVersion(versionId, newFunctionCatalogId);
        }
        else {
            _updateUncommittedFunctionCatalog(functionCatalog);
        }
    }

    public void deleteFunctionCatalogFromVersion(final long versionId, final long functionCatalogId) throws DatabaseException {
        _disassociateFunctionCatalogWithVersion(versionId, functionCatalogId);
        _deleteFunctionCatalogIfUncommitted(functionCatalogId);
    }
}
