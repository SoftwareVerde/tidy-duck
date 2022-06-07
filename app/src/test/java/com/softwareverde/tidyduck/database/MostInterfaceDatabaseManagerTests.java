package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.TestBase;
import com.softwareverde.tidyduck.most.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

public class MostInterfaceDatabaseManagerTests extends TestBase {
    protected DatabaseConnection<Connection> _databaseConnection;
    protected FunctionCatalogDatabaseManager _functionCatalogDatabaseManager;
    protected FunctionBlockDatabaseManager _functionBlockDatabaseManager;
    protected FunctionBlockInflater _functionBlockInflater;
    protected MostInterfaceDatabaseManager _mostInterfaceDatabaseManager;
    protected MostInterfaceInflater _mostInterfaceInflater;
    protected MostFunctionDatabaseManager _mostFunctionDatabaseManager;
    protected MostFunctionInflater _mostFunctionInflater;

    protected FunctionCatalog _createSavedTestFunctionCatalog() throws DatabaseException {
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

        _functionCatalogDatabaseManager.insertFunctionCatalog(functionCatalog);
        return functionCatalog;
    }

    protected FunctionBlock _createSavedTestFunctionBlock(final FunctionCatalog functionCatalog, final String functionBlockName) throws DatabaseException {
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
        functionBlock.setMostId("0xAB");
        functionBlock.setAccess("public");

        _functionBlockDatabaseManager.insertFunctionBlockForFunctionCatalog(functionCatalog.getId(), functionBlock, 1L);
        return functionBlock;
    }

    protected MostInterface _createUnsavedTestMostInterface(final String mostInterfaceName) throws DatabaseException {
        final MostInterface mostInterface = new MostInterface();
        mostInterface.setName(mostInterfaceName);
        mostInterface.setDescription("Description");
        mostInterface.setLastModifiedDate(DateUtil.dateFromDateString("2000-01-01 00:00:00"));
        mostInterface.setMostId("0x10");
        mostInterface.setVersion("1.0");
        mostInterface.setVersion("v1.0.0");

        return mostInterface;
    }

    protected MostFunction _createUnsavedTestMostFunction(final String mostFunctionName) throws DatabaseException {
        final AuthorInflater authorInflater = new AuthorInflater(_databaseConnection);
        final Author author = authorInflater.inflateAuthor(1L);

        final CompanyInflater companyInflater = new CompanyInflater(_databaseConnection);
        final Company company = companyInflater.inflateCompany(1L);

        final MostTypeInflater mostTypeInflater = new MostTypeInflater(_databaseConnection);
        final MostType mostType = mostTypeInflater.inflateMostType(1L);



        final MostFunction mostFunction = new Property();
        mostFunction.setName(mostFunctionName);
        mostFunction.setDescription("Description");
        final MostFunctionStereotype stereotype = new MostFunctionStereotype();
        stereotype.setId(1L);
        stereotype.setName("Event");
        mostFunction.setFunctionStereotype(stereotype);
        mostFunction.setMostId("0x10");
        mostFunction.setRelease("1.0");
        mostFunction.setReturnParameterName("RPN");
        mostFunction.setAuthor(author);
        mostFunction.setCompany(company);
        mostFunction.setReturnType(mostType);

        return mostFunction;
    }

    protected Integer _getTotalMostInterfacesCountInDatabase(final String mostInterfaceName) throws DatabaseException {
        return _databaseConnection.query(new Query("SELECT COUNT(*) AS count FROM interfaces WHERE name = ?").setParameter(mostInterfaceName)).get(0).getInteger("count");
    }

    protected Integer _getTotalMostFunctionsCountInDatabase(final String mostFunctionName) throws DatabaseException {
        return _databaseConnection.query(new Query("SELECT COUNT(*) AS count FROM functions WHERE name = ?").setParameter(mostFunctionName)).get(0).getInteger("count");
    }

    protected Long _getCurrentDatabaseTime() throws DatabaseException {
        final String dateTimeString = _databaseConnection.query(new Query("SELECT NOW() AS time")).get(0).getString("time");
        return DateUtil.datetimeToTimestamp(dateTimeString);
    }

    protected void _randomizeNextFunctionCatalogInsertId() throws DatabaseException {
        final Integer autoIncrement = TestDataLoader.generateRandomAutoIncrementId();
        _databaseConnection.executeDdl(new Query("ALTER TABLE function_catalogs AUTO_INCREMENT="+ autoIncrement));
    }

    protected void _randomizeNextFunctionBlockInsertId() throws DatabaseException {
        final Integer autoIncrement = TestDataLoader.generateRandomAutoIncrementId();
        _databaseConnection.executeDdl(new Query("ALTER TABLE function_blocks AUTO_INCREMENT="+ autoIncrement));
    }

    protected void _randomizeNextMostInterfaceInsertId() throws DatabaseException {
        final Integer autoIncrement = TestDataLoader.generateRandomAutoIncrementId();
        _databaseConnection.executeDdl(new Query("ALTER TABLE interfaces AUTO_INCREMENT="+ autoIncrement));
    }

    protected void _randomizeNextMostFunctionInsertId() throws DatabaseException {
        final Integer autoIncrement = TestDataLoader.generateRandomAutoIncrementId();
        _databaseConnection.executeDdl(new Query("ALTER TABLE functions AUTO_INCREMENT="+ autoIncrement));
    }

    @Before
    public void setup() throws Exception {
        super.setup();
        _databaseConnection = TestBase._database.newConnection();
        _functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(_databaseConnection);
        _functionBlockDatabaseManager = new FunctionBlockDatabaseManager(_databaseConnection);
        _functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
        _mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(_databaseConnection);
        _mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);
        _mostFunctionDatabaseManager = new MostFunctionDatabaseManager(_databaseConnection);
        _mostFunctionInflater = new MostFunctionInflater(_databaseConnection);

        _randomizeNextFunctionCatalogInsertId();
        _randomizeNextFunctionBlockInsertId();
        _randomizeNextMostInterfaceInsertId();
    }

    @Test
    public void should_insert_and_retrieve_single_stored_most_interface_by_its_id() throws Exception {
        // Setup
        final FunctionCatalog functionCatalog = _createSavedTestFunctionCatalog();
        final FunctionBlock functionBlock = _createSavedTestFunctionBlock(functionCatalog, "Function Block");
        final MostInterface mostInterface = _createUnsavedTestMostInterface("Most Interface");

        final Long currentDate = DateUtil.dateFromDateString(DateUtil.dateToDateString(new Date())).getTime();

        // Action
        _mostInterfaceDatabaseManager.insertMostInterfaceForFunctionBlock(functionBlock.getId(), mostInterface);
        final MostInterface inflatedMostInterface = _mostInterfaceInflater.inflateMostInterface(mostInterface.getId());

        // Assert
        Assert.assertEquals("Most Interface", inflatedMostInterface.getName());
        Assert.assertEquals("Description", inflatedMostInterface.getDescription());
        Assert.assertEquals("v1.0.0", inflatedMostInterface.getVersion());
        Assert.assertEquals("0x10", inflatedMostInterface.getMostId());
        Assert.assertEquals(currentDate.longValue(), inflatedMostInterface.getLastModifiedDate().getTime());
    }

    @Test
    public void should_insert_and_retrieve_single_stored_most_interface_by_its_function_block() throws Exception {
        // Setup
        final FunctionCatalog functionCatalog = _createSavedTestFunctionCatalog();
        final FunctionBlock functionBlock = _createSavedTestFunctionBlock(functionCatalog, "Function Block");
        final MostInterface mostInterface = _createUnsavedTestMostInterface("Most Interface");

        final Long currentDate = DateUtil.dateFromDateString(DateUtil.dateToDateString(new Date())).getTime();

        // Action
        _mostInterfaceDatabaseManager.insertMostInterfaceForFunctionBlock(functionBlock.getId(), mostInterface);
        final List<MostInterface> inflatedMostInterfaces = _mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(functionBlock.getId());

        // Assert
        Assert.assertEquals(1, inflatedMostInterfaces.size());

        final MostInterface inflatedMostInterface = inflatedMostInterfaces.get(0);
        Assert.assertEquals("Most Interface", inflatedMostInterface.getName());
        Assert.assertEquals("Description", inflatedMostInterface.getDescription());
        Assert.assertEquals("v1.0.0", inflatedMostInterface.getVersion());
        Assert.assertEquals("0x10", inflatedMostInterface.getMostId());
        Assert.assertEquals(currentDate.longValue(), inflatedMostInterface.getLastModifiedDate().getTime());
    }

    @Test
    public void should_associate_an_existing_most_interface_to_a_new_function_block() throws Exception {
        // Setup
        final FunctionCatalog functionCatalog = _createSavedTestFunctionCatalog();
        final FunctionBlock functionBlock = _createSavedTestFunctionBlock(functionCatalog, "Function Block");
        final FunctionBlock functionBlock2 = _createSavedTestFunctionBlock(functionCatalog, "Function Block 2");

        final MostInterface mostInterface = _createUnsavedTestMostInterface("Most Interface 123");
        _mostInterfaceDatabaseManager.insertMostInterfaceForFunctionBlock(functionBlock.getId(), mostInterface);

        // Action
        _mostInterfaceDatabaseManager.associateMostInterfaceWithFunctionBlock(functionBlock2.getId(), mostInterface.getId());
        final List<MostInterface> inflatedMostInterfaces = _mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(functionBlock2.getId());

        // Assert
        Assert.assertEquals(1, inflatedMostInterfaces.size());

        final MostInterface inflatedMostInterface = inflatedMostInterfaces.get(0);
        Assert.assertEquals("Most Interface 123", inflatedMostInterface.getName());
    }

    @Test
    public void should_not_associate_an_existing_most_interface_to_the_same_function_block() throws Exception {
        // Setup
        final FunctionCatalog functionCatalog = _createSavedTestFunctionCatalog();
        final FunctionBlock functionBlock = _createSavedTestFunctionBlock(functionCatalog, "Function Block");

        final MostInterface mostInterface = _createUnsavedTestMostInterface("Most Interface 123");
        _mostInterfaceDatabaseManager.insertMostInterfaceForFunctionBlock(functionBlock.getId(), mostInterface);

        // Action
        _functionBlockDatabaseManager.associateFunctionBlockWithFunctionCatalog(functionCatalog.getId(), functionBlock.getId());
        final List<MostInterface> inflatedMostInterfaces = _mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(functionBlock.getId());

        // Assert
        Assert.assertEquals(1, inflatedMostInterfaces.size());

        final MostInterface inflatedMostInterface = inflatedMostInterfaces.get(0);
        Assert.assertEquals("Most Interface 123", inflatedMostInterface.getName());
    }

    @Test
    public void should_completely_delete_most_interface_if_not_committed() throws Exception {
        // Setup
        final MostInterface mostInterface = _createUnsavedTestMostInterface("Most Interface");
        _mostInterfaceDatabaseManager.insertOrphanedMostInterface(mostInterface);

        final Integer beforeDeleteMostInterfaceCount = _getTotalMostInterfacesCountInDatabase("Most Interface");
        Assert.assertEquals(1, beforeDeleteMostInterfaceCount.intValue());

        // Action
        _mostInterfaceDatabaseManager.setIsDeletedForMostInterface(mostInterface.getId(), true);
        _mostInterfaceDatabaseManager.deleteMostInterface(mostInterface.getId());

        // Assert
        final Integer mostInterfaceCount = _getTotalMostInterfacesCountInDatabase("Most Interface");
        Assert.assertEquals(0, mostInterfaceCount.intValue());
    }

    @Test
    public void should_trash_function_block_before_deleting() throws Exception {
        // Setup
        final FunctionCatalog functionCatalog = _createSavedTestFunctionCatalog();
        final FunctionBlock functionBlock = _createSavedTestFunctionBlock(functionCatalog, "Function Block");
        final FunctionBlock functionBlock2 = _createSavedTestFunctionBlock(functionCatalog, "Function Block 2");

        final MostInterface mostInterface = _createUnsavedTestMostInterface("Most Interface");
        _mostInterfaceDatabaseManager.insertMostInterfaceForFunctionBlock(functionBlock.getId(), mostInterface);
        _mostInterfaceDatabaseManager.associateMostInterfaceWithFunctionBlock(functionBlock2.getId(), mostInterface.getId());

        final Integer beforeDeleteMostInterfaceCount = _getTotalMostInterfacesCountInDatabase("Most Interface");
        Assert.assertEquals(1, beforeDeleteMostInterfaceCount.intValue());

        // Action
        try {
            _mostInterfaceDatabaseManager.deleteMostInterface(mostInterface.getId());
            Assert.fail("Improperly deleted interface.");
        } catch (Exception e) {
            // expected, do nothing
        }

        // Assert
        final List<MostInterface> inflatedMostInterfaces = _mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(functionBlock.getId());
        Assert.assertEquals(1, inflatedMostInterfaces.size());

        final List<MostInterface> inflatedMostInterfacesForFunctionBlock2 = _mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(functionBlock2.getId());
        Assert.assertEquals(1, inflatedMostInterfacesForFunctionBlock2.size());

        final Integer functionBlockCount = _getTotalMostInterfacesCountInDatabase("Most Interface");
        Assert.assertEquals(1, functionBlockCount.intValue());
    }

    @Test
    public void deleting_most_interface_should_delete_parent_references() throws Exception {
        // Setup
        final FunctionCatalog functionCatalog = _createSavedTestFunctionCatalog();
        final FunctionBlock functionBlock = _createSavedTestFunctionBlock(functionCatalog, "Function Block");

        final MostInterface mostInterface = _createUnsavedTestMostInterface("Most Interface");
        _mostInterfaceDatabaseManager.insertMostInterfaceForFunctionBlock(functionBlock.getId(), mostInterface);

        final Integer beforeDeleteMostInterfaceCount = _getTotalMostInterfacesCountInDatabase("Most Interface");
        Assert.assertEquals(1, beforeDeleteMostInterfaceCount.intValue());

        // Action
        _mostInterfaceDatabaseManager.setIsDeletedForMostInterface(mostInterface.getId(), true);
        _mostInterfaceDatabaseManager.deleteMostInterface(mostInterface.getId());

        // Assert
        final List<MostInterface> inflatedMostInterfaces = _mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(functionBlock.getId());
        Assert.assertEquals(0, inflatedMostInterfaces.size());

        final Integer functionBlockCount = _getTotalMostInterfacesCountInDatabase("Most Interface");
        Assert.assertEquals(0, functionBlockCount.intValue());
    }

    @Test
    public void deleting_most_interface_should_delete_functions() throws Exception {
        // Setup
        final MostInterface mostInterface = _createUnsavedTestMostInterface("Most Interface");
        _mostInterfaceDatabaseManager.insertOrphanedMostInterface(mostInterface);

        final MostFunction mostFunction = _createUnsavedTestMostFunction("Function");
        _mostFunctionDatabaseManager.insertMostFunctionForMostInterface(mostInterface.getId(), mostFunction);

        // Action
        _mostInterfaceDatabaseManager.setIsDeletedForMostInterface(mostInterface.getId(), true);
        _mostInterfaceDatabaseManager.deleteMostInterface(mostInterface.getId());

        // Assert
        List<MostFunction> mostFunctions = _mostFunctionInflater.inflateMostFunctionsFromMostInterfaceId(mostInterface.getId());
        Assert.assertEquals(0, mostFunctions.size());

        final Integer functionBlockCount = _getTotalMostInterfacesCountInDatabase("Most Interface");
        Assert.assertEquals(0, functionBlockCount.intValue());
    }

    @Test
    public void deleting_approved_interface_should_use_permanently_deleted_flag() throws Exception {
        // Setup
        final FunctionCatalog functionCatalog = _createSavedTestFunctionCatalog();
        final FunctionBlock functionBlock = _createSavedTestFunctionBlock(functionCatalog, "Function Block");

        final MostInterface mostInterface = _createUnsavedTestMostInterface("Most Interface");
        _mostInterfaceDatabaseManager.insertMostInterfaceForFunctionBlock(functionBlock.getId(), mostInterface);

        final Integer beforeDeleteMostInterfaceCount = _getTotalMostInterfacesCountInDatabase("Most Interface");
        Assert.assertEquals(1, beforeDeleteMostInterfaceCount.intValue());

        // Action
        _mostInterfaceDatabaseManager.approveMostInterface(mostInterface.getId(), 1L);
        _mostInterfaceDatabaseManager.setIsDeletedForMostInterface(mostInterface.getId(), true);
        _mostInterfaceDatabaseManager.deleteMostInterface(mostInterface.getId());

        // Assert
        final List<MostInterface> inflatedMostInterfaces = _mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(functionBlock.getId());
        Assert.assertEquals(1, inflatedMostInterfaces.size());
        Assert.assertEquals(true, inflatedMostInterfaces.get(0).isDeleted());
        Assert.assertEquals(true, inflatedMostInterfaces.get(0).isPermanentlyDeleted());

        final Integer functionBlockCount = _getTotalMostInterfacesCountInDatabase("Most Interface");
        Assert.assertEquals(1, functionBlockCount.intValue());
    }

    @Test
    public void deleting_approved_most_interface_should_mark_functions_permanently_deleted() throws Exception {
        // Setup
        final MostInterface mostInterface = _createUnsavedTestMostInterface("Most Interface");
        _mostInterfaceDatabaseManager.insertOrphanedMostInterface(mostInterface);

        final MostFunction mostFunction = _createUnsavedTestMostFunction("Function");
        _mostFunctionDatabaseManager.insertMostFunctionForMostInterface(mostInterface.getId(), mostFunction);

        // Action
        _mostInterfaceDatabaseManager.approveMostInterface(mostInterface.getId(), 1L);
        _mostInterfaceDatabaseManager.setIsDeletedForMostInterface(mostInterface.getId(), true);
        _mostInterfaceDatabaseManager.deleteMostInterface(mostInterface.getId());

        // Assert
        List<MostFunction> mostFunctions = _mostFunctionInflater.inflateMostFunctionsFromMostInterfaceId(mostInterface.getId());
        Assert.assertEquals(1, mostFunctions.size());
        Assert.assertEquals(true, mostFunctions.get(0).isDeleted());
        Assert.assertEquals(true, mostFunctions.get(0).isPermanentlyDeleted());

        final Integer functionBlockCount = _getTotalMostFunctionsCountInDatabase("Function");
        Assert.assertEquals(1, functionBlockCount.intValue());
    }

    @Test
    public void should_list_all_function_blocks_containing_a_shared_most_interface() throws Exception {
        // Setup
        final FunctionCatalog functionCatalog = _createSavedTestFunctionCatalog();
        final FunctionBlock functionBlock = _createSavedTestFunctionBlock(functionCatalog, "Function Block");
        final FunctionBlock functionBlock2 = _createSavedTestFunctionBlock(functionCatalog, "Function Block 2");

        final MostInterface mostInterface = _createUnsavedTestMostInterface("Most Interface");
        _mostInterfaceDatabaseManager.insertMostInterfaceForFunctionBlock(functionBlock.getId(), mostInterface);
        _mostInterfaceDatabaseManager.associateMostInterfaceWithFunctionBlock(functionBlock2.getId(), mostInterface.getId());

        // Action
        final List<Long> functionBlockIds = _mostInterfaceDatabaseManager.listFunctionBlocksContainingMostInterface(mostInterface.getId());

        // Assert
        Assert.assertEquals(2, functionBlockIds.size());
        Assert.assertTrue(functionBlockIds.contains(functionBlock.getId()));
        Assert.assertTrue(functionBlockIds.contains(functionBlock2.getId()));
    }
}
