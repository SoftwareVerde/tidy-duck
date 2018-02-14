package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Role;
import com.softwareverde.tidyduck.Settings;
import com.softwareverde.tidyduck.most.Company;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class AccountInflater {

    protected final DatabaseConnection<Connection> _databaseConnection;

    public AccountInflater(DatabaseConnection<Connection> connection) {
        _databaseConnection = connection;
    }

    public List<Account> inflateAccounts() throws DatabaseException {
        return inflateAccounts(false);
    }

    public List<Account> inflateAccounts(final boolean includeDeletedAccounts) throws DatabaseException {
        final Query query;

        if (includeDeletedAccounts) {
            query = new Query("SELECT id FROM accounts");
        }
        else {
            query = new Query("SELECT id FROM accounts WHERE is_deleted = ?")
                .setParameter(false)
            ;
        }

        List<Row> rows = _databaseConnection.query(query);
        List<Account> accounts = new ArrayList<>();
        for (final Row row : rows) {
            final long accountId = row.getLong("id");
            final Account account = inflateAccount(accountId);
            accounts.add(account);
        }
        return accounts;
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
        settings.setDefaultMode(row.getString("default_mode"));

        final Account account = new Account();
        account.setId(accountId);
        account.setName(row.getString("name"));
        account.setUsername(row.getString("username"));
        account.setPassword(row.getString("password"));
        account.setCompany(company);
        account.setSettings(settings);
        account.setRoles(getRoles(accountId));

        return account;
    }

    private List<Role> getRoles(final long accountId) throws DatabaseException {
        final Query query = new Query("SELECT roles.id FROM roles INNER JOIN accounts_roles ON accounts_roles.role_id = roles.id WHERE accounts_roles.account_id = ?");
        query.setParameter(accountId);

        List<Row> rows = _databaseConnection.query(query);

        final RoleInflater roleInflater = new RoleInflater(_databaseConnection);

        List<Role> roles = new ArrayList<>();
        for (final Row row : rows) {
            final long roleId = row.getLong("id");

            final Role role = roleInflater.inflateRole(roleId);
            roles.add(role);
        }
        return roles;
    }
}
