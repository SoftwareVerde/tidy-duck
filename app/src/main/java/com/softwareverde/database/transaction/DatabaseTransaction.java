package com.softwareverde.database.transaction;

import com.softwareverde.database.DatabaseException;

public interface DatabaseTransaction<T> {
    public void execute(DatabaseConnectedRunnable<T> databaseConnectedRunnable) throws DatabaseException;
}
