package com.softwareverde.tidyduck.database;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.mysql.MysqlMemoryDatabase;
import com.softwareverde.tidyduck.most.Author;
import com.softwareverde.tidyduck.most.Company;
import com.softwareverde.tidyduck.most.FunctionBlock;
import com.softwareverde.tidyduck.most.FunctionCatalog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

public class FunctionBlockDatabaseManagerTests {
    protected final Database<Connection> _inMemoryDatabase = new MysqlMemoryDatabase();
    protected DatabaseConnection<Connection> _databaseConnection;
    protected FunctionCatalogDatabaseManager _functionCatalogDatabaseManager;
    protected FunctionBlockDatabaseManager _functionBlockDatabaseManager;
    protected FunctionBlockInflater _functionBlockInflater;

    protected FunctionCatalog _createSavedTestFunctionCatalog() throws DatabaseException {
        final AuthorInflater authorInflater = new AuthorInflater(_databaseConnection);
        final Author author = authorInflater.inflateAuthor(1L);

        final CompanyInflater companyInflater = new CompanyInflater(_databaseConnection);
        final Company company = companyInflater.inflateCompany(1L);

        final FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setName("Name");
        functionCatalog.setRelease("v0.0.0");
        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);

        _functionCatalogDatabaseManager.insertFunctionCatalog(functionCatalog);
        return functionCatalog;
    }

    protected FunctionBlock _createUnsavedTestFunctionBlock(final String functionBlockName) throws DatabaseException {
        final AuthorInflater authorInflater = new AuthorInflater(_databaseConnection);
        final Author author = authorInflater.inflateAuthor(1L);

        final CompanyInflater companyInflater = new CompanyInflater(_databaseConnection);
        final Company company = companyInflater.inflateCompany(1L);

        final FunctionBlock functionBlock = new FunctionBlock();
        functionBlock.setAuthor(author);
        functionBlock.setCompany(company);
        functionBlock.setKind("Proprietary");
        functionBlock.setName(functionBlockName);
        functionBlock.setDescription("Description");
        functionBlock.setRelease("v1.0.0");
        functionBlock.setAccess("public");
        return functionBlock;
    }

    protected Integer _getTotalFunctionBlockCountInDatabase(final String functionBlockName) throws DatabaseException {
        return _databaseConnection.query(new Query("SELECT COUNT(*) AS count FROM function_blocks WHERE name = ?").setParameter(functionBlockName)).get(0).getInteger("count");
    }

    protected void _randomizeNextFunctionCatalogInsertId() throws DatabaseException {
        final Integer autoIncrement = TestDataLoader.generateRandomAutoIncrementId();
        // _databaseConnection.executeDdl(new Query("ALTER TABLE function_catalogs AUTO_INCREMENT = "+ autoIncrement));
        _databaseConnection.executeDdl(new Query("ALTER TABLE function_catalogs ALTER COLUMN id RESTART WITH "+ autoIncrement));
    }

    protected void _randomizeNextFunctionBlockInsertId() throws DatabaseException {
        final Integer autoIncrement = TestDataLoader.generateRandomAutoIncrementId();
        _databaseConnection.executeDdl(new Query("ALTER TABLE function_blocks ALTER COLUMN id RESTART WITH "+ autoIncrement));
    }

    protected void _randomizeNextMostInterfaceInsertId() throws DatabaseException {
        final Integer autoIncrement = TestDataLoader.generateRandomAutoIncrementId();
        _databaseConnection.executeDdl(new Query("ALTER TABLE interfaces ALTER COLUMN id RESTART WITH "+ autoIncrement));
    }

    @Before
    public void setup() throws Exception {
        _databaseConnection = _inMemoryDatabase.newConnection();
        _functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(_databaseConnection);
        _functionBlockDatabaseManager = new FunctionBlockDatabaseManager(_databaseConnection);
        _functionBlockInflater = new FunctionBlockInflater(_databaseConnection);

        TestDataLoader.initDatabase(_databaseConnection);
        TestDataLoader.insertFakeCompany(_databaseConnection);
        TestDataLoader.insertFakeAccount(_databaseConnection);

        _randomizeNextFunctionCatalogInsertId();
        _randomizeNextFunctionBlockInsertId();
        _randomizeNextMostInterfaceInsertId();
    }

    @Test
    public void should_insert_and_retrieve_single_stored_function_block_by_its_id() throws Exception {
        // Setup
        final FunctionCatalog functionCatalog = _createSavedTestFunctionCatalog();
        final FunctionBlock functionBlock = _createUnsavedTestFunctionBlock("Function Block");

        // Action
        _functionBlockDatabaseManager.insertFunctionBlockForFunctionCatalog(functionCatalog.getId(), functionBlock, 1L);
        final FunctionBlock inflatedFunctionBlock = _functionBlockInflater.inflateFunctionBlock(functionBlock.getId());

        // Assert
        Assert.assertEquals(1L, inflatedFunctionBlock.getAuthor().getId().longValue());
        Assert.assertEquals(1L, inflatedFunctionBlock.getCompany().getId().longValue());
        Assert.assertEquals("Proprietary", inflatedFunctionBlock.getKind());
        Assert.assertEquals("Function Block", inflatedFunctionBlock.getName());
        Assert.assertEquals("Description", inflatedFunctionBlock.getDescription());
        Assert.assertEquals("v1.0.0", inflatedFunctionBlock.getRelease());
        Assert.assertEquals("public", inflatedFunctionBlock.getAccess());
    }

    @Test
    public void should_insert_and_retrieve_single_stored_function_block_by_its_function_catalog() throws Exception {
        // Setup
        final FunctionCatalog functionCatalog = _createSavedTestFunctionCatalog();
        final FunctionBlock functionBlock = _createUnsavedTestFunctionBlock("Function Block");

        // Action
        _functionBlockDatabaseManager.insertFunctionBlockForFunctionCatalog(functionCatalog.getId(), functionBlock, 1L);
        final List<FunctionBlock> inflatedFunctionBlocks = _functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalog.getId());

        // Assert
        Assert.assertEquals(1, inflatedFunctionBlocks.size());

        final FunctionBlock inflatedFunctionBlock = inflatedFunctionBlocks.get(0);
        Assert.assertEquals(1L, inflatedFunctionBlock.getAuthor().getId().longValue());
        Assert.assertEquals(1L, inflatedFunctionBlock.getCompany().getId().longValue());
        Assert.assertEquals("Proprietary", inflatedFunctionBlock.getKind());
        Assert.assertEquals("Function Block", inflatedFunctionBlock.getName());
        Assert.assertEquals("Description", inflatedFunctionBlock.getDescription());
        Assert.assertEquals("v1.0.0", inflatedFunctionBlock.getRelease());
        Assert.assertEquals("public", inflatedFunctionBlock.getAccess());
    }

    @Test
    public void should_associate_an_existing_function_block_to_a_new_function_catalog() throws Exception {
        // Setup
        final FunctionCatalog functionCatalog = _createSavedTestFunctionCatalog();
        final FunctionCatalog functionCatalog2 = _createSavedTestFunctionCatalog();

        final FunctionBlock functionBlock = _createUnsavedTestFunctionBlock("Function Block 123");
        _functionBlockDatabaseManager.insertFunctionBlockForFunctionCatalog(functionCatalog.getId(), functionBlock, 1L);

        // Action
        _functionBlockDatabaseManager.associateFunctionBlockWithFunctionCatalog(functionCatalog2.getId(), functionBlock.getId());
        final List<FunctionBlock> inflatedFunctionBlocks = _functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalog2.getId());

        // Assert
        Assert.assertEquals(1, inflatedFunctionBlocks.size());

        final FunctionBlock inflatedFunctionBlock = inflatedFunctionBlocks.get(0);
        Assert.assertEquals("Function Block 123", inflatedFunctionBlock.getName());
    }

    @Test
    public void should_not_associate_an_existing_function_block_to_the_same_function_catalog() throws Exception {
        // Setup
        final FunctionCatalog functionCatalog = _createSavedTestFunctionCatalog();

        final FunctionBlock functionBlock = _createUnsavedTestFunctionBlock("Function Block 123");
        _functionBlockDatabaseManager.insertFunctionBlockForFunctionCatalog(functionCatalog.getId(), functionBlock, 1L);

        // Action
        _functionBlockDatabaseManager.associateFunctionBlockWithFunctionCatalog(functionCatalog.getId(), functionBlock.getId());
        final List<FunctionBlock> inflatedFunctionBlocks = _functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalog.getId());

        // Assert
        Assert.assertEquals(1, inflatedFunctionBlocks.size());

        final FunctionBlock inflatedFunctionBlock = inflatedFunctionBlocks.get(0);
        Assert.assertEquals("Function Block 123", inflatedFunctionBlock.getName());
    }

    @Test
    public void should_completely_delete_function_block_if_not_committed() throws Exception {
        // Setup
        final FunctionCatalog functionCatalog = _createSavedTestFunctionCatalog();

        final FunctionBlock functionBlock = _createUnsavedTestFunctionBlock("Function Block");
        _functionBlockDatabaseManager.insertFunctionBlockForFunctionCatalog(functionCatalog.getId(), functionBlock, 1L);
        final Integer beforeDeleteFunctionBlockCount = _getTotalFunctionBlockCountInDatabase("Function Block");
        Assert.assertEquals(1, beforeDeleteFunctionBlockCount.intValue());

        // Action
        // First call of delete method will disassociate from Function Catalog.
        _functionBlockDatabaseManager.deleteFunctionBlockFromFunctionCatalog(functionCatalog.getId(), functionBlock.getId());
        // Second call of delete method checks if Function Block is orphaned, then deletes it from database.
        // The app provides a Function Catalog ID of 0 for orphaned Function Blocks.
        _functionBlockDatabaseManager.deleteFunctionBlockFromFunctionCatalog(0, functionBlock.getId());

        // Assert
        final List<FunctionBlock> inflatedFunctionBlocks = _functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalog.getId());
        Assert.assertEquals(0, inflatedFunctionBlocks.size());

        final Integer functionBlockCount = _getTotalFunctionBlockCountInDatabase("Function Block");
        Assert.assertEquals(0, functionBlockCount.intValue());
    }

    @Test
    public void should_not_completely_delete_function_block_if_associated_with_another_function_catalog() throws Exception {
        // Setup
        final FunctionCatalog functionCatalog = _createSavedTestFunctionCatalog();
        final FunctionCatalog functionCatalog2 = _createSavedTestFunctionCatalog();

        final FunctionBlock functionBlock = _createUnsavedTestFunctionBlock("Function Block");
        _functionBlockDatabaseManager.insertFunctionBlockForFunctionCatalog(functionCatalog.getId(), functionBlock, 1L);
        _functionBlockDatabaseManager.associateFunctionBlockWithFunctionCatalog(functionCatalog2.getId(), functionBlock.getId());

        final Integer beforeDeleteFunctionBlockCount = _getTotalFunctionBlockCountInDatabase("Function Block");
        Assert.assertEquals(1, beforeDeleteFunctionBlockCount.intValue());

        // Action
        _functionBlockDatabaseManager.deleteFunctionBlockFromFunctionCatalog(functionCatalog.getId(), functionBlock.getId());

        // Assert
        final List<FunctionBlock> inflatedFunctionBlocks = _functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalog.getId());
        Assert.assertEquals(0, inflatedFunctionBlocks.size());

        final List<FunctionBlock> inflatedFunctionBlocksForFunctionCatalog2 = _functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalog2.getId());
        Assert.assertEquals(1, inflatedFunctionBlocksForFunctionCatalog2.size());

        final Integer functionBlockCount = _getTotalFunctionBlockCountInDatabase("Function Block");
        Assert.assertEquals(1, functionBlockCount.intValue());
    }

    @Test
    public void should_list_all_function_catalogs_containing_a_shared_function_block() throws Exception {
        // Setup
        final FunctionCatalog functionCatalog = _createSavedTestFunctionCatalog();
        final FunctionCatalog functionCatalog2 = _createSavedTestFunctionCatalog();

        final FunctionBlock functionBlock = _createUnsavedTestFunctionBlock("Function Block");
        _functionBlockDatabaseManager.insertFunctionBlockForFunctionCatalog(functionCatalog.getId(), functionBlock, 1L);
        _functionBlockDatabaseManager.associateFunctionBlockWithFunctionCatalog(functionCatalog2.getId(), functionBlock.getId());

        // Action
        final List<Long> functionCatalogIds = _functionBlockDatabaseManager.listFunctionCatalogIdsContainingFunctionBlock(functionBlock.getId());

        // Assert
        Assert.assertEquals(2, functionCatalogIds.size());
        Assert.assertTrue(functionCatalogIds.contains(functionCatalog.getId()));
        Assert.assertTrue(functionCatalogIds.contains(functionCatalog2.getId()));
    }
}
