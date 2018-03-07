package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.mysql.MysqlTestDatabase;
import org.junit.Before;

import java.sql.Connection;

public class ReleaseDatabaseManagerTests {
    protected final MysqlTestDatabase _database = new MysqlTestDatabase();
    protected DatabaseConnection<Connection> _databaseConnection;
    protected ReleaseDatabaseManager _releaseDatabaseManager;

    @Before
    public void setup() throws Exception {
        _databaseConnection = _database.newConnection();
        _releaseDatabaseManager = new ReleaseDatabaseManager(_databaseConnection);

        TestDataLoader.initDatabase(_database);
        TestDataLoader.insertFakeCompany(_databaseConnection);
        TestDataLoader.insertFakeAccount(_databaseConnection);
    }
}
