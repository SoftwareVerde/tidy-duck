package com.softwareverde.tidyduck;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.mysql.MysqlTestDatabase;
import com.softwareverde.tidyduck.database.TestDataLoader;

import java.sql.Connection;

public class TestBase {
    protected static MysqlTestDatabase _database;

    public void setup() throws Exception {
        _database = new MysqlTestDatabase();
        _database.reset();

        TestDataLoader.initDatabase(_database);

        try (DatabaseConnection<Connection> _databaseConnection = _database.newConnection()) {
            TestDataLoader.insertFakeCompany(_databaseConnection);
            TestDataLoader.insertFakeAccount(_databaseConnection);
            TestDataLoader.insertFakeMostType(_databaseConnection);
        }
    }
}
