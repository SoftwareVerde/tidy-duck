package com.softwareverde.tidyduck.database;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.mysql.MysqlMemoryDatabase;
import com.softwareverde.tidyduck.Author;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.FunctionBlock;
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
    protected FunctionBlockDatabaseManager _functionBlockDatabaseManager;
    protected FunctionCatalogInflater _functionCatalogInflater;

    @Before
    public void setup() throws Exception {
        _databaseConnection = _inMemoryDatabase.newConnection();
        _functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(_databaseConnection);
        _functionCatalogInflater = new FunctionCatalogInflater(_databaseConnection);
        _functionBlockDatabaseManager = new FunctionBlockDatabaseManager(_databaseConnection);

        TestDataLoader.initDatabase(_databaseConnection);
        TestDataLoader.insertFakeCompany(_databaseConnection);
        TestDataLoader.insertFakeAccount(_databaseConnection);
        TestDataLoader.insertFakeVersion(_databaseConnection);
    }

    @Test
    public void should_insert_and_retrieve_single_stored_function_catalog() throws Exception {
        // Setup
        final AuthorInflater authorInflater = new AuthorInflater(_databaseConnection);
        final Author author = authorInflater.inflateAuthor(1L);

        final CompanyInflater companyInflater = new CompanyInflater(_databaseConnection);
        final Company company = companyInflater.inflateCompany(1L);

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

        final AuthorInflater authorInflater = new AuthorInflater(_databaseConnection);
        final Author author = authorInflater.inflateAuthor(1L);
        final Author newAuthor = authorInflater.inflateAuthor(2L);

        final CompanyInflater companyInflater = new CompanyInflater(_databaseConnection);
        final Company company = companyInflater.inflateCompany(1L);
        final Company newCompany = companyInflater.inflateCompany(2L);

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
        final AuthorInflater authorInflater = new AuthorInflater(_databaseConnection);
        final Author author = authorInflater.inflateAuthor(1L);

        final CompanyInflater companyInflater = new CompanyInflater(_databaseConnection);
        final Company company = companyInflater.inflateCompany(1L);

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

        final FunctionCatalog inflateFunctionCatalog = _functionCatalogInflater.inflateFunctionCatalog(functionCatalog.getId());
        Assert.assertNull(inflateFunctionCatalog);
    }

    @Test
    public void should_inflate_children_of_function_catalog() throws Exception {
        // Setup
        final AuthorInflater authorInflater = new AuthorInflater(_databaseConnection);
        final Author author = authorInflater.inflateAuthor(1L);

        final CompanyInflater companyInflater = new CompanyInflater(_databaseConnection);
        final Company company = companyInflater.inflateCompany(1L);

        final Long versionId = 1L;
        final FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setName("Name");
        functionCatalog.setRelease("v0.0.0");
        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);
        _functionCatalogDatabaseManager.insertFunctionCatalogForVersion(versionId, functionCatalog);

        final FunctionBlock functionBlock = new FunctionBlock();
        functionBlock.setAuthor(author);
        functionBlock.setCompany(company);
        functionBlock.setKind("Proprietary");
        functionBlock.setName("Function Block");
        functionBlock.setDescription("Description");
        functionBlock.setRelease("v1.0.0");
        functionBlock.setAccess("public");
        _functionBlockDatabaseManager.insertFunctionBlockForFunctionCatalog(functionCatalog.getId(), functionBlock);

        // Action
        final List<FunctionCatalog> inflatedFunctionCatalogs = _functionCatalogInflater.inflateFunctionCatalogsFromVersionId(1L, true);

        // Assert
        Assert.assertEquals(1, inflatedFunctionCatalogs.size());

        final FunctionCatalog inflatedFunctionCatalog = inflatedFunctionCatalogs.get(0);
        Assert.assertEquals(1L, inflatedFunctionCatalog.getId().longValue());
        Assert.assertEquals("Name", inflatedFunctionCatalog.getName());
        Assert.assertEquals("v0.0.0", inflatedFunctionCatalog.getRelease());
        Assert.assertEquals(1L, inflatedFunctionCatalog.getAuthor().getId().longValue());
        Assert.assertEquals(1L, inflatedFunctionCatalog.getCompany().getId().longValue());

        final List<FunctionBlock> functionBlocks = inflatedFunctionCatalog.getFunctionBlocks();
        Assert.assertEquals(1L, functionBlocks.size());

        final FunctionBlock inflatedFunctionBlock = functionBlocks.get(0);
        Assert.assertEquals(1L, inflatedFunctionBlock.getAuthor().getId().longValue());
        Assert.assertEquals(1L, inflatedFunctionBlock.getCompany().getId().longValue());
        Assert.assertEquals("Proprietary", inflatedFunctionBlock.getKind());
        Assert.assertEquals("Function Block", inflatedFunctionBlock.getName());
        Assert.assertEquals("Description", inflatedFunctionBlock.getDescription());
        Assert.assertEquals("v1.0.0", inflatedFunctionBlock.getRelease());
        Assert.assertEquals("public", inflatedFunctionBlock.getAccess());
    }

}