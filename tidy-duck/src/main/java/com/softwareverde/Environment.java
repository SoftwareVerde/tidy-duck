package com.softwareverde;

import com.softwareverde.database.Database;

public class Environment {

    private static volatile Environment environment = new Environment();

    protected Environment() {}

    public static Environment getInstance() {
        return environment;
    }

    public Database getDatabase() {
        return null;
    }
}
