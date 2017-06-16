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
        final FunctionBlock.Kind kind = functionBlock.getKind();
        final String name = functionBlock.getName();
        final String description = functionBlock.getDescription();
        final String release = functionBlock.getRelease();
        final Long accountId = functionBlock.getAccount().getId();
        final Long companyId = functionBlock.getCompany().getId();

        final Query query = new Query("INSERT INTO function_blocks (kind, name, description, last_modified_date, release_version, account_id, company_id) VALUES (?, ?, ?, NOW(), ?, ?, ?)")
            .setParameter(kind.getXmlText())
            .setParameter(name)
            .setParameter(description)
            .setParameter(release)
            .setParameter(accountId)
            .setParameter(companyId)
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
}
