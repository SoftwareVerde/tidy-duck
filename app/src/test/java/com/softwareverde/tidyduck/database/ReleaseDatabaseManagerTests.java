package com.softwareverde.tidyduck.database;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.mysql.MysqlMemoryDatabase;
import com.softwareverde.tidyduck.most.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

public class ReleaseDatabaseManagerTests {
    protected final Database<Connection> _inMemoryDatabase = new MysqlMemoryDatabase();
    protected DatabaseConnection<Connection> _databaseConnection;
    protected ReleaseDatabaseManager _releaseDatabaseManager;

    @Before
    public void setup() throws Exception {
        _databaseConnection = _inMemoryDatabase.newConnection();
        _releaseDatabaseManager = new ReleaseDatabaseManager(_databaseConnection);

        TestDataLoader.initDatabase(_databaseConnection);
        TestDataLoader.insertFakeCompany(_databaseConnection);
        TestDataLoader.insertFakeAccount(_databaseConnection);
    }
}
