package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Permission;
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

        loadPermissions(account);

        return account;
    }

    private void loadPermissions(final Account account) throws DatabaseException {
        final Query query = new Query("SELECT role_permissions.* FROM role_permissions INNER JOIN accounts_roles ON accounts_roles.role_id = role_permissions.role_id WHERE account_id = ?");
        query.setParameter(account.getId());

        List<Row> rows = _databaseConnection.query(query);

        for (final Row row : rows) {
            addPermission(account, Permission.ADMIN_CREATE_USERS,       row.getBoolean("admin_create_users"));
            addPermission(account, Permission.ADMIN_MODIFY_USERS,       row.getBoolean("admin_modify_users"));
            addPermission(account, Permission.ADMIN_DELETE_USERS,       row.getBoolean("admin_delete_users"));
            addPermission(account, Permission.ADMIN_RESET_PASSWORD,     row.getBoolean("admin_reset_password"));
            addPermission(account, Permission.MOST_COMPONENTS_RELEASE,  row.getBoolean("most_components_release"));
            addPermission(account, Permission.MOST_COMPONENTS_CREATE,   row.getBoolean("most_components_create"));
            addPermission(account, Permission.MOST_COMPONENTS_MODIFY,   row.getBoolean("most_components_modify"));
            addPermission(account, Permission.MOST_COMPONENTS_VIEW,     row.getBoolean("most_components_view"));
            addPermission(account, Permission.TYPES_CREATE,             row.getBoolean("types_create"));
            addPermission(account, Permission.TYPES_MODIFY,             row.getBoolean("types_modify"));
            addPermission(account, Permission.REVIEWS_APPROVAL,         row.getBoolean("reviews_approval"));
            addPermission(account, Permission.REVIEWS_COMMENTS,         row.getBoolean("reviews_comments"));
            addPermission(account, Permission.REVIEWS_VOTING,           row.getBoolean("reviews_voting"));
            addPermission(account, Permission.REVIEWS_VIEW,             row.getBoolean("reviews_view"));
        }
    }

    private void addPermission(final Account account, final Permission permission, final boolean shouldHavePermission) {
        if (shouldHavePermission) {
            account.addPermission(permission);
        }
    }
}
