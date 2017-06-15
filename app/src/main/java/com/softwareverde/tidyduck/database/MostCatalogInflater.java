package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.*;
import com.softwareverde.util.Util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class MostCatalogInflater {

    protected final DatabaseConnection<Connection> _databaseConnection;

    public MostCatalogInflater(DatabaseConnection<Connection> connection) {
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
        final CompanyInflater companyInflater = new CompanyInflater(_databaseConnection);

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

        final Author account = _inflateAccount(row.getLong("account_id"));

        final Company company = companyInflater.inflateCompany(row.getLong("company_id"));

        final FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setId(Util.parseLong(row.getString("id")));
        functionCatalog.setName(row.getString("name"));
        functionCatalog.setRelease(row.getString("release_version"));
        functionCatalog.setReleaseDate(DateUtil.dateFromDateString(row.getString("release_date")));
        functionCatalog.setAuthor(account);
        functionCatalog.setCompany(company);

        return functionCatalog;
    }

    private Author _inflateAccount(Long accountId) throws DatabaseException {
        final AccountInflater accountInflater = new AccountInflater(_databaseConnection);
        final Account account = accountInflater.inflateAccount(accountId);

        Author author = new Author();
        author.setId(account.getId());
        author.setName(account.getName());
        author.setCompany(account.getCompany());

        return author;
    }
}
