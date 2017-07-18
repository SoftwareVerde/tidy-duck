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
import java.util.List;

public class FunctionCatalogInflater {
    protected final Logger _logger = new Slf4jLogger(this.getClass());
    protected final DatabaseConnection<Connection> _databaseConnection;

    public FunctionCatalogInflater(DatabaseConnection<Connection> connection) {
        _databaseConnection = connection;
    }

    /**
     * Inflates function catalogs without child objects.
     * @param versionId
     * @return
     * @throws DatabaseException
     */
    public List<FunctionCatalog> inflateFunctionCatalogsFromVersionId(final long versionId) throws DatabaseException {
        return inflateFunctionCatalogsFromVersionId(versionId, false);
    }

    public List<FunctionCatalog> inflateFunctionCatalogsFromVersionId(final long versionId, final boolean inflateChildren) throws DatabaseException {
        final Query query = new Query(
                "SELECT function_catalog_id"
                + " FROM versions_function_catalogs"
                + " WHERE version_id = ?"
        );
        query.setParameter(versionId);

        final ArrayList<FunctionCatalog> functionCatalogs = new ArrayList<>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final long functionCatalogId = row.getLong("function_catalog_id");
            FunctionCatalog functionCatalog = inflateFunctionCatalog(functionCatalogId, inflateChildren);
            functionCatalogs.add(functionCatalog);
        }
        return functionCatalogs;
    }

    /**
     * Returns the FunctionCatalog for the specified functionCatalogId.
     *  FunctionCatalog's children are NOT inflated.
     *  Returns null if a FunctionCatalog with the functionCatalogId is not found.
     */
    public FunctionCatalog inflateFunctionCatalog(final long functionCatalogId) throws DatabaseException {
        return inflateFunctionCatalog(functionCatalogId, false);
    }

    /**
     * Returns the FunctionCatalog for the specified functionCatalogId.
     *  If inflateChildren is true, the FunctionCatalog and all of its children are inflated.
     *  Returns null if a FunctionCatalog with the functionCatalogId is not found.
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

        final Long id = Util.parseLong(row.getString("id"));
        final String name = row.getString("name");
        final String release = row.getString("release_version");
        final Long accountId = row.getLong("account_id");
        final Long companyId = row.getLong("company_id");
        final boolean isCommitted = row.getBoolean("is_committed");

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
        functionCatalog.setIsCommitted(isCommitted);

        if (inflateChildren) {
            FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
            List<FunctionBlock> functionBlocks = functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalogId, inflateChildren);
            functionCatalog.setFunctionBlocks(functionBlocks);
        }

        return functionCatalog;
    }

}
