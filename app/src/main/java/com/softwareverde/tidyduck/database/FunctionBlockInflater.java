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
import java.util.*;

public class FunctionBlockInflater {

    private final DatabaseConnection<Connection> _databaseConnection;

    public FunctionBlockInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<FunctionBlock> inflateFunctionBlocks() throws DatabaseException {
        final Query query = new Query(
            "SELECT * FROM function_blocks"
        );

        final List<FunctionBlock> functionBlocks = new ArrayList<FunctionBlock>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final FunctionBlock functionBlock = _convertRowToFunctionBlock(row);
            functionBlocks.add(functionBlock);
        }
        return functionBlocks;
    }


    public Map<Long, List<FunctionBlock>> inflateFunctionBlocksGroupedByBaseVersionId() throws DatabaseException {
        List<FunctionBlock> functionBlocks = inflateFunctionBlocks();
        return _groupByBaseVersionId(functionBlocks);
    }

    private Map<Long, List<FunctionBlock>> _groupByBaseVersionId(final List<FunctionBlock> functionBlocks) {
        final HashMap<Long, List<FunctionBlock>> groupedFunctionBlocks = new HashMap<>();

        for (final FunctionBlock functionBlock : functionBlocks) {
            Long baseVersionId = functionBlock.getBaseVersionId();
            if (!groupedFunctionBlocks.containsKey(baseVersionId)) {
                groupedFunctionBlocks.put(baseVersionId, new ArrayList<FunctionBlock>());
            }
            groupedFunctionBlocks.get(baseVersionId).add(functionBlock);
        }

        return groupedFunctionBlocks;
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
        final FunctionBlock functionBlock = _convertRowToFunctionBlock(row);

        if (inflateChildren) {
            _inflateChildren(functionBlock);
        }

        return functionBlock;
    }

    private void _inflateChildren(final FunctionBlock functionBlock) throws DatabaseException {
        final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);
        final List<MostInterface> mostInterfaces = mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(functionBlock.getId(),true );
        functionBlock.setMostInterfaces(mostInterfaces);
    }

    public Map<Long, List<FunctionBlock>> inflateFunctionBlocksMatchingSearchString(final String searchString, final Long accountId) throws DatabaseException {
        // Recall that "LIKE" is case-insensitive for MySQL: https://stackoverflow.com/a/14007477/3025921
        final Query query = new Query ("SELECT * FROM function_blocks\n" +
                                        "WHERE base_version_id IN (" +
                                            "SELECT DISTINCT function_blocks.base_version_id\n" +
                                            "FROM function_blocks\n" +
                                            "WHERE function_blocks.name LIKE ?)\n" +
                                            "AND (is_approved = ? OR creator_account_id = ? OR creator_account_id IS NULL)");
        query.setParameter("%" + searchString + "%");
        query.setParameter(true);
        query.setParameter(accountId);


        List<FunctionBlock> functionBlocks = new ArrayList<>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            FunctionBlock functionBlock = _convertRowToFunctionBlock(row);
            functionBlocks.add(functionBlock);
        }
        return _groupByBaseVersionId(functionBlocks);
    }

    private FunctionBlock _convertRowToFunctionBlock(final Row row) throws DatabaseException {
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
        final boolean isSource = row.getBoolean("is_source");
        final boolean isSink = row.getBoolean("is_sink");
        final boolean isApproved = row.getBoolean("is_approved");
        final boolean isReleased = row.getBoolean("is_released");
        final Long baseVersionId = row.getLong("base_version_id");
        final Long priorVersionId = row.getLong("prior_version_id");
        final Long creatorAccountId = row.getLong("creator_account_id");

        final AuthorInflater authorInflater = new AuthorInflater(_databaseConnection);
        final Author author = authorInflater.inflateAuthor(accountId);
        final CompanyInflater companyInflater = new CompanyInflater(_databaseConnection);
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
        functionBlock.setIsSource(isSource);
        functionBlock.setIsSink(isSink);
        functionBlock.setIsApproved(isApproved);
        functionBlock.setIsReleased(isReleased);
        functionBlock.setBaseVersionId(baseVersionId);
        functionBlock.setPriorVersionId(priorVersionId);
        functionBlock.setCreatorAccountId(creatorAccountId);

        return functionBlock;
    }
}
