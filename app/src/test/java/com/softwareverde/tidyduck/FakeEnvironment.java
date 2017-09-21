package com.softwareverde.tidyduck;

import com.softwareverde.database.DatabaseException;
import com.softwareverde.tidyduck.environment.Environment;

public class FakeEnvironment extends Environment {
    @Override
    public void _initDatabase() {
        _database = null;
    }

    public FakeEnvironment() throws DatabaseException { }
}
