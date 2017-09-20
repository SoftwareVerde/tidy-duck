package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Permission;
import com.softwareverde.tidyduck.Role;
import com.softwareverde.tidyduck.Settings;
import com.softwareverde.tidyduck.most.Company;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class AccountInflater {

    protected final DatabaseConnection<Connection> _databaseConnection;

    public AccountInflater(DatabaseConnection<Connection> connection) {
        _databaseConnection = connection;
    }

    public List<Account> inflateAccounts() throws DatabaseException {
        final Query query = new Query("SELECT id FROM accounts");

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
        final Query query = new Query("SELECT roles.* FROM roles INNER JOIN accounts_roles ON accounts_roles.role_id = roles.id WHERE accounts_roles.account_id = ?");
        query.setParameter(accountId);

        List<Row> rows = _databaseConnection.query(query);

        List<Role> roles = new ArrayList<>();
        for (final Row row : rows) {
            final long roleId = row.getLong("id");
            final String roleName = row.getString("name");

            final Role role = new Role();

            role.setName(roleName);
            role.setPermissions(getPermissions(roleId));

            roles.add(role);
        }
        return roles;
    }

    private List<Permission> getPermissions(final long roleId) throws DatabaseException {
        final Query query = new Query("SELECT role_permissions.* FROM role_permissions WHERE role_id = ?");
        query.setParameter(roleId);

        List<Row> rows = _databaseConnection.query(query);

        List<Permission> permissions = new ArrayList<>();
        for (final Row row : rows) {
            addPermission(permissions, Permission.ADMIN_CREATE_USERS,       row.getBoolean("admin_create_users"));
            addPermission(permissions, Permission.ADMIN_MODIFY_USERS,       row.getBoolean("admin_modify_users"));
            addPermission(permissions, Permission.ADMIN_DELETE_USERS,       row.getBoolean("admin_delete_users"));
            addPermission(permissions, Permission.ADMIN_RESET_PASSWORD,     row.getBoolean("admin_reset_password"));
            addPermission(permissions, Permission.MOST_COMPONENTS_RELEASE,  row.getBoolean("most_components_release"));
            addPermission(permissions, Permission.MOST_COMPONENTS_CREATE,   row.getBoolean("most_components_create"));
            addPermission(permissions, Permission.MOST_COMPONENTS_MODIFY,   row.getBoolean("most_components_modify"));
            addPermission(permissions, Permission.MOST_COMPONENTS_VIEW,     row.getBoolean("most_components_view"));
            addPermission(permissions, Permission.TYPES_CREATE,             row.getBoolean("types_create"));
            addPermission(permissions, Permission.TYPES_MODIFY,             row.getBoolean("types_modify"));
            addPermission(permissions, Permission.REVIEWS_APPROVAL,         row.getBoolean("reviews_approval"));
            addPermission(permissions, Permission.REVIEWS_COMMENTS,         row.getBoolean("reviews_comments"));
            addPermission(permissions, Permission.REVIEWS_VOTING,           row.getBoolean("reviews_voting"));
            addPermission(permissions, Permission.REVIEWS_VIEW,             row.getBoolean("reviews_view"));
        }
        return permissions;
    }

    private void addPermission(final List<Permission> permissions, final Permission permission, final boolean shouldHavePermission) {
        if (shouldHavePermission) {
            permissions.add(permission);
        }
    }
}
