package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.logging.Logger;
import com.softwareverde.logging.slf4j.Slf4jLogger;
import com.softwareverde.tidyduck.most.Author;
import com.softwareverde.tidyduck.most.Company;
import com.softwareverde.tidyduck.most.FunctionBlock;
import com.softwareverde.tidyduck.most.FunctionCatalog;
import com.softwareverde.util.Util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionCatalogInflater {
    protected final Logger _logger = new Slf4jLogger(this.getClass());
    protected final DatabaseConnection<Connection> _databaseConnection;

    public FunctionCatalogInflater(DatabaseConnection<Connection> connection) {
        _databaseConnection = connection;
    }

    /**
     * <p>Inflates function catalogs without child objects.</p>
     * @return
     * @throws DatabaseException
     */
    public List<FunctionCatalog> inflateFunctionCatalogs() throws DatabaseException {
        return inflateFunctionCatalogs(false);
    }

    public List<FunctionCatalog> inflateFunctionCatalogs(final boolean inflateChildren) throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM function_catalogs ORDER BY base_version_id"
        );

        final ArrayList<FunctionCatalog> functionCatalogs = new ArrayList<>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            FunctionCatalog functionCatalog = convertRowToFunctionCatalog(row);

            if (inflateChildren) {
                inflateChildren(functionCatalog);
            }
            functionCatalogs.add(functionCatalog);
        }

        return functionCatalogs;
    }

    public Map<Long, List<FunctionCatalog>> inflateFunctionCatalogsGroupedByBaseVersionId() throws DatabaseException {
        List<FunctionCatalog> functionCatalogs = inflateFunctionCatalogs(false);
        return groupByBaseVersionId(functionCatalogs);
    }

    private Map<Long, List<FunctionCatalog>> groupByBaseVersionId(final List<FunctionCatalog> functionCatalogs) {
        final HashMap<Long, List<FunctionCatalog>> groupedFunctionCatalogs = new HashMap<>();

        for (final FunctionCatalog functionCatalog : functionCatalogs) {
            Long baseVersionId = functionCatalog.getBaseVersionId();
            if (!groupedFunctionCatalogs.containsKey(baseVersionId)) {
                groupedFunctionCatalogs.put(baseVersionId, new ArrayList<FunctionCatalog>());
            }
            groupedFunctionCatalogs.get(baseVersionId).add(functionCatalog);
        }

        return groupedFunctionCatalogs;
    }

    /**
     * <p>Returns the FunctionCatalog for the specified functionCatalogId.</p>
     *  <p>FunctionCatalog's children are NOT inflated.</p>
     *  <p>Returns null if a FunctionCatalog with the functionCatalogId is not found.</p>
     */
    public FunctionCatalog inflateFunctionCatalog(final long functionCatalogId) throws DatabaseException {
        return inflateFunctionCatalog(functionCatalogId, false);
    }

    /**
     * <p>Returns the FunctionCatalog for the specified functionCatalogId.</p>
     *  <p>If inflateChildren is true, the FunctionCatalog and all of its children are inflated.</p>
     *  <p>Returns null if a FunctionCatalog with the functionCatalogId is not found.</p>
     */
    public FunctionCatalog inflateFunctionCatalog(final long functionCatalogId, final boolean inflateChildren) throws DatabaseException {
        final Query query = new Query(
            "SELECT * FROM function_catalogs WHERE id = ?"
        );
        query.setParameter(functionCatalogId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            _logger.warn("Could not find functionCatalog w/ ID: "+ functionCatalogId);
            return null;
        }

        // get first (should be only) row
        final Row row = rows.get(0);
        FunctionCatalog functionCatalog = convertRowToFunctionCatalog(row);

        if (inflateChildren) {
            inflateChildren(functionCatalog);
        }

        return functionCatalog;
    }

    private FunctionCatalog convertRowToFunctionCatalog(final Row row) throws DatabaseException {
        final Long id = Util.parseLong(row.getString("id"));
        final String name = row.getString("name");
        final String release = row.getString("release_version");
        final Long accountId = row.getLong("account_id");
        final Long companyId = row.getLong("company_id");
        final boolean isReleased = row.getBoolean("is_released");
        final Long baseVersionId = row.getLong("base_version_id");
        final Long priorVersionId = row.getLong("prior_version_id");

        final AuthorInflater authorInflater = new AuthorInflater(_databaseConnection);
        final Author author = authorInflater.inflateAuthor(accountId);

        final CompanyInflater companyInflater = new CompanyInflater(_databaseConnection);
        final Company company = companyInflater.inflateCompany(companyId);

        final FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setId(id);
        functionCatalog.setName(name);
        functionCatalog.setRelease(release);
        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);
        functionCatalog.setReleased(isReleased);
        functionCatalog.setBaseVersionId(baseVersionId);
        functionCatalog.setPriorVersionId(priorVersionId);

        return functionCatalog;
    }

    private void inflateChildren(final FunctionCatalog functionCatalog) throws DatabaseException {
        FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
        List<FunctionBlock> functionBlocks = functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalog.getId(), true);
        functionCatalog.setFunctionBlocks(functionBlocks);
    }

}
