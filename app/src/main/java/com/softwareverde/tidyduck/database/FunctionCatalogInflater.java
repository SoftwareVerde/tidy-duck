package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.*;
import com.softwareverde.util.Util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FunctionCatalogInflater {

    protected final DatabaseConnection<Connection> _databaseConnection;

    public FunctionCatalogInflater(DatabaseConnection<Connection> connection) {
        _databaseConnection = connection;
    }

    public List<FunctionCatalog> inflateFunctionCatalogsFromVersionId(long versionId) throws DatabaseException {
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
            FunctionCatalog functionCatalog = inflateFunctionCatalog(functionCatalogId);
            functionCatalogs.add(functionCatalog);
        }
        return functionCatalogs;
    }

    public FunctionCatalog inflateFunctionCatalog(long functionCatalogId) throws DatabaseException {

        final Query query = new Query(
                "SELECT *"
                + " FROM function_catalogs"
                + " WHERE id = ?"
        );
        query.setParameter(functionCatalogId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Function catalog ID " + functionCatalogId + " not found.");
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
        functionCatalog.setCommitted(isCommitted);

        FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
        List<FunctionBlock> functionBlocks = functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalogId);
        functionCatalog.setFunctionBlocks(functionBlocks);

        return functionCatalog;
    }

}
