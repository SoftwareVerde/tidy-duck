package com.softwareverde.tidyduck;

import com.softwareverde.database.DatabaseException;
import com.softwareverde.tidyduck.environment.TidyDuckEnvironment;

public class FakeTidyDuckEnvironment extends TidyDuckEnvironment {
    @Override
    public void _initDatabase() {
        _database = null;
    }

    public FakeTidyDuckEnvironment() throws DatabaseException { }
}
