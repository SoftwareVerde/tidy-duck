package com.softwareverde.tidyduck;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.mysql.MysqlTestDatabase;
import com.softwareverde.tidyduck.database.TestDataLoader;

import java.sql.Connection;

public class TestBase {
    protected static final MysqlTestDatabase _database = new MysqlTestDatabase();

    public void setup() throws Exception {
        _database.reset();
        DatabaseConnection<Connection> _databaseConnection = _database.newConnection();

        TestDataLoader.initDatabase(_database);
        TestDataLoader.insertFakeCompany(_databaseConnection);
        TestDataLoader.insertFakeAccount(_databaseConnection);
        TestDataLoader.insertFakeMostType(_databaseConnection);
    }
}
