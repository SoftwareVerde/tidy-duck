package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.FunctionBlock;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FunctionBlockInflater {

    private final DatabaseConnection<Connection> _databaseConnection;

    public FunctionBlockInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<FunctionBlock> inflateFunctionBlocksFromFunctionCatalogId(long functionCatalogId) throws DatabaseException {
        final Query query = new Query(
                "SELECT function_block_id"
                        + " FROM function_catalogs_function_blocks"
                        + " WHERE function_catalog_id = ?"
        );
        query.setParameter(functionCatalogId);

        List<FunctionBlock> functionBlocks = new ArrayList<FunctionBlock>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final long functionBlockId = row.getLong("function_block_id");
            FunctionBlock functionBlock = inflateFunctionBlock(functionBlockId);
            functionBlocks.add(functionBlock);
        }
        return functionBlocks;
    }

    public FunctionBlock inflateFunctionBlock(final long functionBlockId) throws DatabaseException {
        final Query query = new Query(
                "SELECT *"
                        + " FROM function_blocks"
                        + " WHERE id = ?"
        );
        query.setParameter(functionBlockId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Function block ID " + functionBlockId + " not found.");
        }
        // get first (should be only) row
        final Row row = rows.get(0);

        final long id = row.getLong("id");
        final String mostId = row.getString("most_id");
        final String kindString = row.getString("kind");
        final FunctionBlock.Kind kind = FunctionBlock.Kind.valueOf(kindString.toUpperCase());
        final String name = row.getString("name");
        final String description = row.getString("description");
        final Date lastModifiedDate = DateUtil.dateFromDateString(row.getString("last_modified_date"));
        final String release = row.getString("release_version");
        final long accountId = row.getLong("account_id");
        final long companyId = row.getLong("company_id");

        MostCatalogInflater mostCatalogInflater = new MostCatalogInflater(_databaseConnection);
        final Account account = mostCatalogInflater.inflateAccount(accountId);
        final Company company = mostCatalogInflater.inflateCompany(companyId);

        FunctionBlock functionBlock = new FunctionBlock();
        functionBlock.setId(id);
        functionBlock.setMostId(mostId);
        functionBlock.setKind(kind);
        functionBlock.setName(name);
        functionBlock.setDescription(description);
        functionBlock.setLastModifiedDate(lastModifiedDate);
        functionBlock.setRelease(release);
        functionBlock.setAccount(account);
        functionBlock.setCompany(company);

        return functionBlock;
    }
}
