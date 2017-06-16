package com.softwareverde.tidyduck.database;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.mysql.MysqlMemoryDatabase;
import com.softwareverde.tidyduck.Author;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.FunctionCatalog;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;

public class FunctionCatalogInflaterTests {
    @Test
    public void should_insert_function_catalog_into_database() throws Exception {
        // Setup
        final Database<Connection> inMemoryDatabase = new MysqlMemoryDatabase();
        final DatabaseConnection<Connection> databaseConnection = inMemoryDatabase.newConnection();

        final FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
        final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(databaseConnection);

        TestDataLoader.initDatabase(databaseConnection);
        TestDataLoader.insertFakeCompany(databaseConnection);
        TestDataLoader.insertFakeAccount(databaseConnection);
        TestDataLoader.insertFakeVersion(databaseConnection);

        final Author account = new Author();
        account.setId(1L);

        final Company company = new Company();
        company.setId(1L);

        final Long versionId = 1L;
        final FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setName("Name");
        functionCatalog.setRelease("v0.0.0");
        functionCatalog.setReleaseDate(DateUtil.dateFromDateString("2000-01-01"));
        functionCatalog.setAuthor(account);
        functionCatalog.setCompany(company);
        functionCatalogDatabaseManager.insertFunctionCatalogForVersion(versionId, functionCatalog);

        // Action
        final FunctionCatalog inflatedFunctionCatalog = functionCatalogInflater.inflateFunctionCatalog(1L);

        // Assert
        Assert.assertEquals(1L, inflatedFunctionCatalog.getId().longValue());
        Assert.assertEquals("Name", inflatedFunctionCatalog.getName());
        Assert.assertEquals("v0.0.0", inflatedFunctionCatalog.getRelease());
        Assert.assertEquals("2000-01-01", DateUtil.timestampToDateString(inflatedFunctionCatalog.getReleaseDate().getTime()));
        Assert.assertEquals(1L, inflatedFunctionCatalog.getAuthor().getId().longValue());
        Assert.assertEquals(1L, inflatedFunctionCatalog.getCompany().getId().longValue());
    }
}
