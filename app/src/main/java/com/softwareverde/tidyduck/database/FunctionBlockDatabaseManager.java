package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.tidyduck.FunctionBlock;

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
        final FunctionBlock.Kind kind = functionBlock.getKind();
        final String name = functionBlock.getName();
        final String description = functionBlock.getDescription();
        final String release = functionBlock.getRelease();
        final Long authorId = functionBlock.getAuthor().getId();
        final Long companyId = functionBlock.getCompany().getId();
        final String access = functionBlock.getAccess();

        final Query query = new Query("INSERT INTO function_blocks (most_id, kind, name, description, last_modified_date, release_version, account_id, company_id, access) VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?)")
            .setParameter(mostId)
            .setParameter(kind.getXmlText())
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
        FunctionBlockInflater functionCatalogInflater = new FunctionBlockInflater(_databaseConnection);
        FunctionBlock functionBlock = functionCatalogInflater.inflateFunctionBlock(functionBlockId);

        if (!functionBlock.isCommitted()) {
            // function block isn't committed, we can delete it
            // TODO: delete interfaces from function block
            _deleteFunctionBlockFromDatabase(functionBlockId);
        }
    }

    private void _deleteFunctionBlockFromDatabase(final long functionBlockId) throws DatabaseException {
        final Query query = new Query("DELETE FROM function_blocks WHERE id = ?")
            .setParameter(functionBlockId)
        ;

        _databaseConnection.executeSql(query);
    }
}
