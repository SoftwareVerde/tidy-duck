package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.FunctionCatalog;
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

        final Account account = inflateAccount(row.getLong("account_id"));

        final Company company = inflateCompany(row.getLong("company_id"));

        final FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setId(Util.parseLong(row.getString("id")));
        functionCatalog.setName(row.getString("name"));
        functionCatalog.setRelease(row.getString("release_version"));
        functionCatalog.setReleaseDate(DateUtil.dateFromDateString(row.getString("release_date")));
        functionCatalog.setAccount(account);
        functionCatalog.setCompany(company);

        return functionCatalog;
    }

    private Company inflateCompany(long companyId) throws DatabaseException {
        final Query query = new Query(
                "SELECT id, name"
                + " FROM companies"
                + " WHERE id = ?"
        );
        query.setParameter(companyId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Company ID " + companyId + " not found.");
        }
        // get first (should be only) row
        final Row row = rows.get(0);

        final Company company = new Company();
        company.setId(row.getLong("id"));
        company.setName(row.getString("name"));

        return company;
    }

    private Account inflateAccount(Long accountId) throws DatabaseException {
        final Query query = new Query(
                "SELECT id, name, company_id"
                + " FROM accounts"
                + " WHERE id = ?"
        );
        query.setParameter(accountId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Account ID " + accountId + " not found.");
        }
        // get first (should be only) row
        final Row row = rows.get(0);

        final Company company = inflateCompany(row.getLong("company_id"));

        Account account = new Account();
        account.setId(row.getLong("id"));
        account.setName(row.getString("name"));
        account.setCompany(company);

        return account;
    }
}
