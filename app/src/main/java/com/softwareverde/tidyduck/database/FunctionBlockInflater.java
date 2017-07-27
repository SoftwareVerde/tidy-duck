package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.most.Author;
import com.softwareverde.tidyduck.most.Company;
import com.softwareverde.tidyduck.most.FunctionBlock;
import com.softwareverde.tidyduck.most.MostInterface;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FunctionBlockInflater {

    private final DatabaseConnection<Connection> _databaseConnection;

    public FunctionBlockInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<FunctionBlock> inflateFunctionBlocksFromFunctionCatalogId(final long functionCatalogId) throws DatabaseException {
        return inflateFunctionBlocksFromFunctionCatalogId(functionCatalogId, false);
    }

    public List<FunctionBlock> inflateFunctionBlocksFromFunctionCatalogId(final long functionCatalogId, final boolean inflateChildren) throws DatabaseException {
        final Query query = new Query(
            "SELECT function_block_id FROM function_catalogs_function_blocks WHERE function_catalog_id = ?"
        );
        query.setParameter(functionCatalogId);

        List<FunctionBlock> functionBlocks = new ArrayList<FunctionBlock>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final long functionBlockId = row.getLong("function_block_id");
            FunctionBlock functionBlock = inflateFunctionBlock(functionBlockId, inflateChildren);
            functionBlocks.add(functionBlock);
        }
        return functionBlocks;
    }

    public FunctionBlock inflateFunctionBlock(final long functionBlockId) throws DatabaseException {
        return inflateFunctionBlock(functionBlockId, false);
    }

    public FunctionBlock inflateFunctionBlock(final long functionBlockId, final boolean inflateChildren) throws DatabaseException {
        final Query query = new Query(
            "SELECT * FROM function_blocks WHERE id = ?"
        );
        query.setParameter(functionBlockId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Function block ID " + functionBlockId + " not found.");
        }

        final Row row = rows.get(0);

        final Long id = row.getLong("id");
        final String mostId = row.getString("most_id");
        final String kind = row.getString("kind");
        final String name = row.getString("name");
        final String description = row.getString("description");
        final Date lastModifiedDate = DateUtil.dateFromDateString(row.getString("last_modified_date"));
        final String release = row.getString("release_version");
        final Long accountId = row.getLong("account_id");
        final Long companyId = row.getLong("company_id");
        final String access = row.getString("access");
        final boolean isReleased = row.getBoolean("is_released");

        AuthorInflater authorInflater = new AuthorInflater(_databaseConnection);
        final Author author = authorInflater.inflateAuthor(accountId);
        CompanyInflater companyInflater = new CompanyInflater(_databaseConnection);
        final Company company = companyInflater.inflateCompany(companyId);

        FunctionBlock functionBlock = new FunctionBlock();
        functionBlock.setId(id);
        functionBlock.setMostId(mostId);
        functionBlock.setKind(kind);
        functionBlock.setName(name);
        functionBlock.setDescription(description);
        functionBlock.setLastModifiedDate(lastModifiedDate);
        functionBlock.setRelease(release);
        functionBlock.setAuthor(author);
        functionBlock.setCompany(company);
        functionBlock.setAccess(access);
        functionBlock.setReleased(isReleased);

        if (inflateChildren) {
            MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);
            List<MostInterface> mostInterfaces = mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(functionBlockId, true);
            functionBlock.setMostInterfaces(mostInterfaces);
        }

        return functionBlock;
    }

    public List<FunctionBlock> inflateFunctionBlocksMatchingSearchString(String searchString) throws DatabaseException {
        // Recall that "LIKE" is case-insensitive for MySQL: https://stackoverflow.com/a/14007477/3025921
        final Query query = new Query ("SELECT DISTINCT function_blocks.id\n" +
                                        "FROM function_blocks\n" +
                                        "WHERE function_blocks.name LIKE ?");
        query.setParameter("%" + searchString + "%");

        List<FunctionBlock> functionBlocks = new ArrayList<>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final long functionBlockId = row.getLong("id");
            FunctionBlock functionBlock = inflateFunctionBlock(functionBlockId);
            functionBlocks.add(functionBlock);
        }
        return functionBlocks;
    }
}
