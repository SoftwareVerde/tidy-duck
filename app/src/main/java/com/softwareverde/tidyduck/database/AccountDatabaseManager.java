package com.softwareverde.tidyduck.database;

import com.softwareverde.database.*;
import com.softwareverde.security.SecureHashUtil;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Settings;
import com.softwareverde.tidyduck.util.Util;


import java.sql.Connection;
import java.util.List;

class AccountDatabaseManager {

    private final DatabaseConnection<Connection> _databaseConnection;

    public AccountDatabaseManager(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public boolean insertAccount(final Account account) throws DatabaseException {
        final String username = account.getUsername();
        if (! _isUsernameUnique(username)) {
            return false;
        }

        final String password = SecureHashUtil.generateRandomPassword();
        final String passwordHash = SecureHashUtil.hashWithPbkdf2(password);
        final String name = account.getName();
        final Long companyId = account.getCompany().getId();

        final Query query = new Query("INSERT INTO accounts (username, password, name, company_id) VALUES (?, ?, ?, ?)")
                .setParameter(username)
                .setParameter(passwordHash)
                .setParameter(name)
                .setParameter(companyId)
        ;

        final long accountId = _databaseConnection.executeSql(query);
        account.setId(accountId);
        account.setPassword(password);

        return true;
    }

    public void updateAccountSettings(final long accountId, final Settings settings) throws DatabaseException {
        final Query query = new Query("UPDATE accounts SET theme = ? WHERE id = ?")
            .setParameter(settings.getTheme())
            .setParameter(accountId)
        ;

        _databaseConnection.executeSql(query);
    }

    public boolean changePassword(final long accountId, final String oldPassword, final String newPassword) throws DatabaseException {
        if (_validateCurrentPassword(accountId, oldPassword)) {
            _changePassword(accountId, newPassword);
            return true;
        }

        return false;
    }

    private void _changePassword(final long accountId, final String newPassword) throws DatabaseException {
        final String newPasswordHash = SecureHashUtil.hashWithPbkdf2(newPassword);
        final Query query = new Query("UPDATE accounts SET password = ? WHERE id = ?")
                .setParameter(newPasswordHash)
                .setParameter(accountId)
        ;

        _databaseConnection.executeSql(query);
    }

    private boolean _validateCurrentPassword(final Long id, final String password) throws DatabaseException {
        final Query query = new Query("SELECT password FROM accounts WHERE id = ?")
                .setParameter(id)
        ;

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.isEmpty()) {
            return false;
        }
        final String storedPassword = rows.get(0).getString("password");

        return SecureHashUtil.validateHashWithPbkdf2(password, storedPassword);
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
}
