package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.tidyduck.Settings;


import java.sql.Connection;

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

    public void changePassword(final long accountId, final String oldPassword, final String newPasswordHash) throws DatabaseException {
        // TODO: Validate original password before setting the new one.
        _changePassword(accountId, newPasswordHash);
    }

    private void _changePassword(final long accountId, final String newPasswordHash) throws DatabaseException {
        final Query query = new Query("UPDATE accounts SET password = ? WHERE id = ?")
                .setParameter(newPasswordHash)
                .setParameter(accountId)
        ;

        _databaseConnection.executeSql(query);
    }
}
