package com.softwareverde.tidyduck.database;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.mysql.MysqlMemoryDatabase;
import com.softwareverde.tidyduck.Author;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.FunctionCatalog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

public class FunctionCatalogDatabaseManagerTests {
    protected final Database<Connection> _inMemoryDatabase = new MysqlMemoryDatabase();
    protected DatabaseConnection<Connection> _databaseConnection;
    protected FunctionCatalogDatabaseManager _functionCatalogDatabaseManager;
    protected FunctionCatalogInflater _functionCatalogInflater;

    @Before
    public void setup() throws Exception {
        _databaseConnection = _inMemoryDatabase.newConnection();
        _functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(_databaseConnection);
        _functionCatalogInflater = new FunctionCatalogInflater(_databaseConnection);

        TestDataLoader.initDatabase(_databaseConnection);
        TestDataLoader.insertFakeCompany(_databaseConnection);
        TestDataLoader.insertFakeAccount(_databaseConnection);
        TestDataLoader.insertFakeVersion(_databaseConnection);
    }

    @Test
    public void should_insert_and_retrieve_single_stored_function_catalog() throws Exception {
        // Setup
        final Author author = new Author();
        author.setId(1L);

        final Company company = new Company();
        company.setId(1L);

        final Long versionId = 1L;
        final FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setName("Name");
        functionCatalog.setRelease("v0.0.0");
        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);

        // Action
        _functionCatalogDatabaseManager.insertFunctionCatalogForVersion(versionId, functionCatalog);
        final List<FunctionCatalog> inflatedFunctionCatalogs = _functionCatalogInflater.inflateFunctionCatalogsFromVersionId(1L);

        // Assert
        Assert.assertEquals(1, inflatedFunctionCatalogs.size());

        final FunctionCatalog inflatedFunctionCatalog = inflatedFunctionCatalogs.get(0);
        Assert.assertEquals(1L, inflatedFunctionCatalog.getId().longValue());
        Assert.assertEquals("Name", inflatedFunctionCatalog.getName());
        Assert.assertEquals("v0.0.0", inflatedFunctionCatalog.getRelease());
        Assert.assertEquals(1L, inflatedFunctionCatalog.getAuthor().getId().longValue());
        Assert.assertEquals(1L, inflatedFunctionCatalog.getCompany().getId().longValue());
    }

    @Test
    public void should_update_an_existing_uncommitted_function_catalog() throws Exception {
        // Setup
        TestDataLoader.insertFakeCompany(_databaseConnection, "Company 2");
        TestDataLoader.insertFakeAccount(_databaseConnection, "Account 2", 2L);

        final Author author = new Author();
        author.setId(1L);

        final Company company = new Company();
        company.setId(1L);

        final Author newAuthor = new Author();
        newAuthor.setId(2L);

        final Company newCompany = new Company();
        newCompany.setId(2L);

        final Long versionId = 1L;
        final FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setName("Name");
        functionCatalog.setRelease("v0.0.0");
        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);

        // Save the function catalog's initial state.
        _functionCatalogDatabaseManager.insertFunctionCatalogForVersion(versionId, functionCatalog);

        // Change the function catalog's properties (to values that were not saved in the database)...
        functionCatalog.setAuthor(newAuthor);
        functionCatalog.setCompany(newCompany);
        functionCatalog.setName("New Name");
        functionCatalog.setRelease("v0.0.1");

        // Action
        _functionCatalogDatabaseManager.updateFunctionCatalogForVersion(1L, functionCatalog);
        final FunctionCatalog inflatedFunctionCatalog = _functionCatalogInflater.inflateFunctionCatalog(functionCatalog.getId());

        // Assert
        Assert.assertEquals(false, inflatedFunctionCatalog.isCommitted());
        Assert.assertEquals(1L, inflatedFunctionCatalog.getId().longValue());
        Assert.assertEquals("New Name", inflatedFunctionCatalog.getName());
        Assert.assertEquals("v0.0.1", inflatedFunctionCatalog.getRelease());
        Assert.assertEquals(2L, inflatedFunctionCatalog.getAuthor().getId().longValue());
        Assert.assertEquals(2L, inflatedFunctionCatalog.getCompany().getId().longValue());
    }

    @Test
    public void should_completely_delete_an_existing_uncommitted_function_catalog() throws Exception {
        // Setup
        final Author author = new Author();
        author.setId(1L);

        final Company company = new Company();
        company.setId(1L);

        final Long versionId = 1L;
        final FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setName("Name");
        functionCatalog.setRelease("v0.0.0");
        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);

        _functionCatalogDatabaseManager.insertFunctionCatalogForVersion(versionId, functionCatalog);

        // Action
        _functionCatalogDatabaseManager.deleteFunctionCatalogFromVersion(1L, functionCatalog.getId());

        // Assert
        final List<FunctionCatalog> functionCatalogs = _functionCatalogInflater.inflateFunctionCatalogsFromVersionId(1L);
        Assert.assertEquals(0, functionCatalogs.size());

        Boolean functionCatalogExistsViaVersion = true;
        try {
            _functionCatalogInflater.inflateFunctionCatalog(functionCatalog.getId());
        }
        catch (final InvalidFunctionCatalogIdException invalidFunctionCatalogIdException) {
            functionCatalogExistsViaVersion = false;
        }
        Assert.assertFalse(functionCatalogExistsViaVersion);
    }

}