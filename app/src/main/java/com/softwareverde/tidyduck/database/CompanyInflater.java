package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.Company;

import java.sql.Connection;
import java.util.List;

public class CompanyInflater {
    protected final DatabaseConnection<Connection> _databaseConnection;

    public CompanyInflater(final DatabaseConnection<Connection> connection) {
        _databaseConnection = connection;
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
