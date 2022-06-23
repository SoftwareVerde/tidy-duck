package com.softwareverde.tidyduck.database;

import com.softwareverde.cryptography.argon2.Argon2;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.database.row.Row;
import com.softwareverde.security.SecureHashUtil;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.AccountId;
import com.softwareverde.tidyduck.Role;
import com.softwareverde.tidyduck.Settings;
import com.softwareverde.tidyduck.most.Company;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

class AccountDatabaseManager {

    private final DatabaseConnection<Connection> _databaseConnection;

    public AccountDatabaseManager(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public boolean insertAccount(final Account account) throws DatabaseException {
        final String username = account.getUsername();
        if (! _isUsernameUnique(username)) {
            final long duplicateAccountId = _isAccountUsernameMarkedAsDeleted(username);
            if (duplicateAccountId > 0) {
                _reactivateDeletedAccount(AccountId.wrap(duplicateAccountId), account);
                return true;
            }
            else {
                return false;
            }
        }

        final String password = SecureHashUtil.generateRandomPassword();
        final String passwordHash = new Argon2().generateParameterizedHash(password.getBytes());
        final String name = account.getName();
        final Long companyId = account.getCompany().getId();
        final List<Role> roles = new ArrayList<>(account.getRoles());

        final Query query = new Query("INSERT INTO accounts (username, password, name, company_id) VALUES (?, ?, ?, ?)")
                .setParameter(username)
                .setParameter(passwordHash)
                .setParameter(name)
                .setParameter(companyId)
        ;

        final AccountId accountId = AccountId.wrap(_databaseConnection.executeSql(query));
        account.setId(accountId);
        account.setPassword(password);

        for (final Role role : roles) {
            _addRole(accountId, role.getId());
        }

        return true;
    }

    public void markAccountAsDeleted(final AccountId accountId) throws DatabaseException {
        final Query query = new Query("UPDATE accounts SET is_deleted = ? WHERE id = ?")
                .setParameter(true)
                .setParameter(accountId)
        ;

        _databaseConnection.executeSql(query);
        _deleteExistingRoles(accountId);
    }

    private void _reactivateDeletedAccount(final AccountId accountId, final Account account) throws DatabaseException {
        final String password = SecureHashUtil.generateRandomPassword();
        final String passwordHash = new Argon2().generateParameterizedHash(password.getBytes());
        final String name = account.getName();
        final Long companyId = account.getCompany().getId();
        final List<Role> roles = new ArrayList<>(account.getRoles());

        final Query query = new Query("UPDATE accounts SET password = ?, name = ?, company_id = ?, is_deleted = ? WHERE id = ?")
                .setParameter(passwordHash)
                .setParameter(name)
                .setParameter(companyId)
                .setParameter(false)
                .setParameter(accountId)
        ;

        _databaseConnection.executeSql(query);
        account.setId(accountId);
        account.setPassword(password);

        for (final Role role : roles) {
            _addRole(accountId, role.getId());
        }
    }

    public boolean insertCompany(final Company company) throws DatabaseException {
        final String companyName = company.getName();
        if (! _isCompanyNameUnique(companyName)) {
            return false;
        }

        final Query query = new Query("INSERT INTO companies (name) VALUES (?)")
                .setParameter(companyName)
        ;

        final long companyId = _databaseConnection.executeSql(query);
        company.setId(companyId);

        return true;
    }

    public void updateAccountSettings(final AccountId accountId, final Settings settings) throws DatabaseException {
        final Query query = new Query("UPDATE accounts SET theme = ?, default_mode = ? WHERE id = ?")
            .setParameter(settings.getTheme())
            .setParameter(settings.getDefaultMode())
            .setParameter(accountId)
        ;

        _databaseConnection.executeSql(query);
    }

    public boolean updateAccountMetadata(final Account account, final boolean isNewUsernameDifferent) throws DatabaseException {
        final String newUsername = account.getUsername();
        if (isNewUsernameDifferent) {
            if (!_isUsernameUnique(newUsername)) {
                return false;
            }
        }

        final AccountId accountId = account.getId();
        final String newName = account.getName();
        final long newCompanyId = account.getCompany().getId();

        final Query query = new Query("UPDATE accounts SET username = ?, name = ?, company_id = ? WHERE id = ?")
                .setParameter(newUsername)
                .setParameter(newName)
                .setParameter(newCompanyId)
                .setParameter(accountId)
        ;

        _databaseConnection.executeSql(query);

        return true;
    }

    public String resetPassword(final AccountId accountId) throws DatabaseException {
        final String newPassword = SecureHashUtil.generateRandomPassword();
        _changePassword(accountId, newPassword);

        return newPassword;
    }

    public boolean changePassword(final AccountId accountId, final String oldPassword, final String newPassword) throws DatabaseException {
        if (_validateCurrentPassword(accountId, oldPassword)) {
            _changePassword(accountId, newPassword);
            return true;
        }

        return false;
    }

    private void _changePassword(final AccountId accountId, final String newPassword) throws DatabaseException {
        final String newPasswordHash = new Argon2().generateParameterizedHash(newPassword.getBytes());
        final Query query = new Query("UPDATE accounts SET password = ? WHERE id = ?")
                .setParameter(newPasswordHash)
                .setParameter(accountId)
        ;

        _databaseConnection.executeSql(query);
    }

    private boolean _validateCurrentPassword(final AccountId id, final String password) throws DatabaseException {
        final Query query = new Query("SELECT password FROM accounts WHERE id = ?")
                .setParameter(id)
        ;

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.isEmpty()) {
            return false;
        }
        final String storedPassword = rows.get(0).getString("password");

        final Argon2 argon2 = new Argon2(storedPassword);
        final String newHash = argon2.generateParameterizedHash(password.getBytes());

        return storedPassword.equals(newHash);
    }

    private boolean _isUsernameUnique(final String username) throws DatabaseException {
        final Query query = new Query("SELECT COUNT(*) AS duplicate_count FROM accounts WHERE username = ?")
                .setParameter(username)
        ;

        final List<Row> rows = _databaseConnection.query(query);

        final Row row = rows.get(0);
        final long duplicateCount = row.getLong("duplicate_count");
        return (duplicateCount == 0);
    }

    private long _isAccountUsernameMarkedAsDeleted(final String username) throws DatabaseException {
        final Query query = new Query("SELECT * FROM accounts WHERE username = ?")
                .setParameter(username)
        ;

        final List<Row> rows = _databaseConnection.query(query);
        final Row row = rows.get(0);

        if (row.getBoolean("is_deleted")) {
            return row.getLong("id");
        }
        else {
            return -1;
        }
    }

    private boolean _isCompanyNameUnique(final String companyName) throws DatabaseException {
        final Query query = new Query("SELECT COUNT(*) AS duplicate_count FROM companies WHERE name = ?")
                .setParameter(companyName)
                ;

        final List<Row> rows = _databaseConnection.query(query);

        final Row row = rows.get(0);
        final long duplicateCount = row.getLong("duplicate_count");
        return (duplicateCount == 0);
    }

    public void updateAccountRoles(final AccountId accountId, final List<Role> roles) throws DatabaseException {
        _deleteExistingRoles(accountId);
        for (final Role role : roles) {
            _addRole(accountId, role.getId());
        }
    }

    private void _deleteExistingRoles(final AccountId accountId) throws DatabaseException {
        final Query query = new Query("DELETE FROM accounts_roles WHERE account_id = ?");
        query.setParameter(accountId);

        _databaseConnection.executeSql(query);
    }

    private void _addRole(final AccountId accountId, final Long roleId) throws DatabaseException {
        final Query query = new Query("INSERT INTO accounts_roles (account_id, role_id) VALUES (?, ?)");
        query.setParameter(accountId);
        query.setParameter(roleId);

        _databaseConnection.executeSql(query);
    }
}
