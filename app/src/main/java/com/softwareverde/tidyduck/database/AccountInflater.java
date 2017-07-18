package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Settings;
import com.softwareverde.tidyduck.most.Company;

import java.sql.Connection;
import java.util.List;

public class AccountInflater {

    protected final DatabaseConnection<Connection> _databaseConnection;

    public AccountInflater(DatabaseConnection<Connection> connection) {
        _databaseConnection = connection;
    }

    public Account inflateAccount(final Long accountId) throws DatabaseException {
        final CompanyInflater companyInflater = new CompanyInflater(_databaseConnection);

        final Query query = new Query("SELECT * FROM accounts WHERE id = ?");
        query.setParameter(accountId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Account Id " + accountId + " not found.");
        }
        final Row row = rows.get(0);

        final Company company = companyInflater.inflateCompany(row.getLong("company_id"));

        final Settings settings = new Settings();
        settings.setTheme(row.getString("theme"));

        final Account account = new Account();
        account.setId(row.getLong("id"));
        account.setName(row.getString("name"));
        account.setUsername(row.getString("username"));
        account.setPassword(row.getString("password"));
        account.setCompany(company);
        account.setSettings(settings);
        return account;
    }
}
