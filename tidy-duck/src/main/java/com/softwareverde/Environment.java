package com.softwareverde;

import java.sql.Connection;

public class Environment {

    private static volatile Environment environment = new Environment();

    protected Environment() {}

    public static Environment getInstance() {
        return environment;
    }

    public Connection getDatabase() {
        return null;
    }
}
