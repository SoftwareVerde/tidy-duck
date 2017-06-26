package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.tidyduck.FunctionBlock;
import com.softwareverde.tidyduck.MostInterface;

import java.util.List;

public class FunctionBlockDatabaseManager {

    private final DatabaseConnection _databaseConnection;

    public FunctionBlockDatabaseManager(final DatabaseConnection databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public void insertFunctionBlockForFunctionCatalog(final Long functionCatalogId, final FunctionBlock functionBlock) throws DatabaseException {
        // TODO: check to see whether function catalog is committed.
        _insertFunctionBlock(functionBlock);
        _associateFunctionBlockWithFunctionCatalog(functionCatalogId, functionBlock.getId());
    }

    private void _insertFunctionBlock(final FunctionBlock functionBlock) throws DatabaseException {
        final String mostId = functionBlock.getMostId();
        final String kind = functionBlock.getKind();
        final String name = functionBlock.getName();
        final String description = functionBlock.getDescription();
        final String release = functionBlock.getRelease();
        final Long authorId = functionBlock.getAuthor().getId();
        final Long companyId = functionBlock.getCompany().getId();
        final String access = functionBlock.getAccess();

        final Query query = new Query("INSERT INTO function_blocks (most_id, kind, name, description, last_modified_date, release_version, account_id, company_id, access) VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?)")
            .setParameter(mostId)
            .setParameter(kind)
            .setParameter(name)
            .setParameter(description)
            .setParameter(release)
            .setParameter(authorId)
            .setParameter(companyId)
            .setParameter(access)
        ;

        final long functionBlockId = _databaseConnection.executeSql(query);
        functionBlock.setId(functionBlockId);
    }

    private Long _associateFunctionBlockWithFunctionCatalog(final long functionCatalogId, final long functionBlockId) throws DatabaseException {
        final Query query = new Query("INSERT INTO function_catalogs_function_blocks (function_catalog_id, function_block_id) VALUES (?, ?)")
            .setParameter(functionCatalogId)
            .setParameter(functionBlockId)
        ;

        return _databaseConnection.executeSql(query);
    }

    public void updateFunctionBlockForFunctionCatalog (final long functionCatalogId, final FunctionBlock proposedFunctionBlock) throws DatabaseException {
        final long inputFunctionBlockId = proposedFunctionBlock.getId();

        FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
        FunctionBlock databaseFunctionBlock = functionBlockInflater.inflateFunctionBlock(inputFunctionBlockId);
        if (!databaseFunctionBlock.isCommitted()) {
            // not committed, can update existing function block
            _updateUncommittedFunctionBlock(proposedFunctionBlock);
        } else {
            // current block is committed to a function catalog
            // need to insert a new function block replace this one
            _insertFunctionBlock(proposedFunctionBlock);
            final long newFunctionBlockId = proposedFunctionBlock.getId();
            // change association with function catalog
            _disassociateFunctionBlockWithFunctionCatalog(functionCatalogId, inputFunctionBlockId);
            _associateFunctionBlockWithFunctionCatalog(functionCatalogId, newFunctionBlockId);
        }
    }

    private void _updateUncommittedFunctionBlock(FunctionBlock proposedFunctionBlock) throws DatabaseException {
        final String newMostId = proposedFunctionBlock.getMostId();
        final String newKind = proposedFunctionBlock.getKind();
        final String newName = proposedFunctionBlock.getName();
        final String newReleaseVersion = proposedFunctionBlock.getRelease();
        final String newDescription = proposedFunctionBlock.getDescription();
        final String newAccess = proposedFunctionBlock.getAccess();
        final long newAuthorId = proposedFunctionBlock.getAuthor().getId();
        final long newCompanyId = proposedFunctionBlock.getCompany().getId();
        final long functionBlockId = proposedFunctionBlock.getId();

        final Query query = new Query("UPDATE function_blocks SET most_id = ?, kind = ?, name = ?, description = ?, last_modified_date = NOW(), release_version = ?, account_id = ?, company_id = ?, access = ? WHERE id = ?")
                .setParameter(newMostId)
                .setParameter(newKind)
                .setParameter(newName)
                .setParameter(newDescription)
                .setParameter(newReleaseVersion)
                .setParameter(newAuthorId)
                .setParameter(newCompanyId)
                .setParameter(newAccess)
                .setParameter(functionBlockId)
                ;

        _databaseConnection.executeSql(query);
    }

    public void deleteFunctionBlockFromFunctionCatalog(final long functionCatalogId, final long functionBlockId) throws DatabaseException {
        _disassociateFunctionBlockWithFunctionCatalog(functionCatalogId, functionBlockId);
        _deleteFunctionBlockIfUncommitted(functionBlockId);
    }

    private void _disassociateFunctionBlockWithFunctionCatalog(long functionCatalogId, long functionBlockId) throws DatabaseException {
        final Query query = new Query("DELETE FROM function_catalogs_function_blocks WHERE function_catalog_id = ? and function_block_id = ?")
            .setParameter(functionCatalogId)
            .setParameter(functionBlockId)
        ;

        _databaseConnection.executeSql(query);
    }

    private void _deleteFunctionBlockIfUncommitted(long functionBlockId) throws DatabaseException {
        FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
        FunctionBlock functionBlock = functionBlockInflater.inflateFunctionBlock(functionBlockId);

        if (!functionBlock.isCommitted()) {
            _deleteInterfacesFromFunctionBlock(functionBlockId);
            _deleteFunctionBlockFromDatabase(functionBlockId);
        }
    }

    private void _deleteInterfacesFromFunctionBlock(long functionBlockId) throws DatabaseException {
        MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);
        List<MostInterface> mostInterfaces = mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(functionBlockId);

        MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(_databaseConnection);
        for (MostInterface mostInterface : mostInterfaces) {
            // function block isn't committed, we can delete it
            mostInterfaceDatabaseManager.deleteMostInterfaceFromFunctionBlock(functionBlockId, mostInterface.getId());
        }
    }

    private void _deleteFunctionBlockFromDatabase(final long functionBlockId) throws DatabaseException {
        final Query query = new Query("DELETE FROM function_blocks WHERE id = ?")
            .setParameter(functionBlockId)
        ;

        _databaseConnection.executeSql(query);
    }
}
