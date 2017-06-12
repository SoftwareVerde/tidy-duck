package com.softwareverde.tidyduck.database;


import com.softwareverde.database.Database;
import com.softwareverde.database.mysql.MysqlMemoryDatabase;
import org.junit.Test;

import java.sql.Connection;

public class MostCatalogInflaterTests {
    @Test
    public void should_insert_function_catalog_into_database() throws Exception {
        // Setup
        final Database<Connection> inMemoryDatabase = new MysqlMemoryDatabase();
        final MostCatalogInflater mostCatalogInflater = new MostCatalogInflater(inMemoryDatabase.newConnection());

        // Action
        mostCatalogInflater.

        // Assert
    }
}
