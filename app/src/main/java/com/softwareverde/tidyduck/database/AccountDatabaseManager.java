package com.softwareverde.tidyduck.database;

import com.softwareverde.database.*;
import com.softwareverde.security.SecureHashUtil;
import com.softwareverde.tidyduck.Settings;


import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.util.List;

class AccountDatabaseManager {

    private final DatabaseConnection<Connection> _databaseConnection;

    public AccountDatabaseManager(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public void updateAccountSettings(final long accountId, final Settings settings) throws DatabaseException {
        Query query = new Query("UPDATE accounts SET theme = ? WHERE id = ?")
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

        final Row row = rows.get(0);
        final String storedPassword = row.getString("password");

        return SecureHashUtil.validateHashWithPbkdf2(password, storedPassword);
    }
}
