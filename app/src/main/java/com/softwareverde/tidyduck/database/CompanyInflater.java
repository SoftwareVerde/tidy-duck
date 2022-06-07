package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.database.row.Row;
import com.softwareverde.tidyduck.most.Company;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class CompanyInflater {
    protected final DatabaseConnection<Connection> _databaseConnection;

    public CompanyInflater(final DatabaseConnection<Connection> connection) {
        _databaseConnection = connection;
    }

    public List<Company> inflateAllCompanies() throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM companies"
        );

        final List<Company> companies = new ArrayList<>();
        final List<Row> rows = _databaseConnection.query(query);

        for (final Row row : rows) {
            final Company company = new Company();
            company.setId(row.getLong("id"));
            company.setName(row.getString("name"));
            companies.add(company);
        }

        return companies;
    }

    public Company inflateCompany(long companyId) throws DatabaseException {
        final Query query = new Query(
            "SELECT id, name FROM companies WHERE id = ?"
        );
        query.setParameter(companyId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Company ID " + companyId + " not found.");
        }
        final Row row = rows.get(0);

        final Company company = new Company();
        company.setId(row.getLong("id"));
        company.setName(row.getString("name"));
        return company;
    }
}
