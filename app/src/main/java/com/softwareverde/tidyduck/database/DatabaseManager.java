package com.softwareverde.tidyduck.database;

import com.softwareverde.database.*;
import com.softwareverde.database.jdbc.transaction.DatabaseRunnable;
import com.softwareverde.database.jdbc.transaction.JdbcDatabaseTransaction;
import com.softwareverde.tidyduck.*;
import com.softwareverde.tidyduck.most.*;


import java.sql.Connection;
import java.util.List;

public class DatabaseManager {
    private final Database<Connection> _database;

    public DatabaseManager(final Database database) {
        _database = database;
    }

    protected void _executeTransaction(final DatabaseRunnable<Connection> databaseRunnable) throws DatabaseException {
        final JdbcDatabaseTransaction jdbcDatabaseTransaction = new JdbcDatabaseTransaction(_database);
        jdbcDatabaseTransaction.execute(databaseRunnable);
    }

    protected <T> T _executeTransaction(final DatabaseCallable<T, Connection> databaseCallable) throws DatabaseException {
        final CallableDatabaseTransaction<T> callableDatabaseTransaction = new CallableDatabaseTransaction<>(_database);
        return callableDatabaseTransaction.call(databaseCallable);
    }

    // APPLICATION SETTINGS

    public void updateSetting(final ApplicationSetting applicationSetting, final String value) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final ApplicationSettingsDatabaseManager applicationSettingsDatabaseManager = new ApplicationSettingsDatabaseManager(databaseConnection);
                applicationSettingsDatabaseManager.updateSetting(applicationSetting, value);
            }
        });
    }

    // ACCOUNT METHODS
    public boolean insertAccount(final Account account) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Boolean, Connection>() {
            @Override
            public Boolean call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final AccountDatabaseManager accountDatabaseManager = new AccountDatabaseManager(databaseConnection);
                return accountDatabaseManager.insertAccount(account);
            }
        });
    }

    public void updateAccountSettings(final AccountId accountId, final Settings settings) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final AccountDatabaseManager accountDatabaseManager = new AccountDatabaseManager(databaseConnection);
                accountDatabaseManager.updateAccountSettings(accountId, settings);
            }
        });
    }

    public boolean updateAccountMetadata(final Account account, final boolean isNewUsernameDifferent) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Boolean, Connection>() {
            @Override
            public Boolean call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final AccountDatabaseManager accountDatabaseManager = new AccountDatabaseManager(databaseConnection);
                return accountDatabaseManager.updateAccountMetadata(account, isNewUsernameDifferent);
            }
        });
    }

    public boolean changePassword(final AccountId accountId, final String oldPassword, final String newPasswordHash) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Boolean, Connection>() {
            @Override
            public Boolean call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final AccountDatabaseManager accountDatabaseManager = new AccountDatabaseManager(databaseConnection);
                return accountDatabaseManager.changePassword(accountId, oldPassword, newPasswordHash);
            }
        });
    }

    public String resetPassword(final AccountId accountId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<String, Connection>() {
            @Override
            public String call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final AccountDatabaseManager accountDatabaseManager = new AccountDatabaseManager(databaseConnection);
                return accountDatabaseManager.resetPassword(accountId);
            }
        });
    }

    public boolean insertCompany(final Company company) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Boolean, Connection>() {
            @Override
            public Boolean call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final AccountDatabaseManager accountDatabaseManager = new AccountDatabaseManager(databaseConnection);
                return accountDatabaseManager.insertCompany(company);
            }
        });
    }

    public void markAccountAsDeleted(final AccountId accountId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final AccountDatabaseManager accountDatabaseManager = new AccountDatabaseManager(databaseConnection);
                accountDatabaseManager.markAccountAsDeleted(accountId);
            }
        });
    }

    // FUNCTION CATALOG METHODS

    public void insertFunctionCatalog(final FunctionCatalog functionCatalog) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                functionCatalogDatabaseManager.insertFunctionCatalog(functionCatalog);
            }
        });
    }

    public void updateFunctionCatalog(final FunctionCatalog functionCatalog, final AccountId accountId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                functionCatalogDatabaseManager.updateFunctionCatalog(functionCatalog, accountId);
            }
        });
    }

    public long forkFunctionCatalog(final long functionCatalogId, final AccountId accountId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Long, Connection>() {
            @Override
            public Long call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                return functionCatalogDatabaseManager.forkFunctionCatalog(functionCatalogId, accountId);
            }
        });
    }

    public void setIsDeletedForFunctionCatalog(final long functionCatalogId, final boolean isDeleted) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                functionCatalogDatabaseManager.setIsDeletedForFunctionCatalog(functionCatalogId, isDeleted);
            }
        });
    }

    public long restoreFunctionCatalogFromTrash(final long functionCatalogId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Long, Connection>() {
            @Override
            public Long call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                return functionCatalogDatabaseManager.restoreFunctionCatalogFromTrash(functionCatalogId);
            }
        });
    }

    public void deleteFunctionCatalog(final long functionCatalogId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                functionCatalogDatabaseManager.deleteFunctionCatalog(functionCatalogId);
            }
        });
    }

    public void submitFunctionCatalogForReview(final Long functionCatalogId, final AccountId accountId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                functionCatalogDatabaseManager.submitFunctionCatalogForReview(functionCatalogId, accountId);
            }
        });
    }

    public boolean isFunctionCatalogApproved(final Long functionCatalogId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Boolean, Connection>() {
            @Override
            public Boolean call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                return functionCatalogDatabaseManager.isApproved(functionCatalogId);
            }
        });
    }

    public FunctionCatalog checkForDuplicateFunctionCatalog(final String functionCatalogName, final Long functionCatalogVersionSeries) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<FunctionCatalog, Connection>() {
            @Override
            public FunctionCatalog call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                return functionCatalogDatabaseManager.checkForDuplicateFunctionCatalog(functionCatalogName, functionCatalogVersionSeries);
            }
        });
    }

    public boolean functionCatalogHasDeletedChildren(final long functionCatalogId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Boolean, Connection>() {
            @Override
            public Boolean call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                return functionCatalogDatabaseManager.hasDeletedChildren(functionCatalogId);
            }
        });
    }

    // RELEASE

    public List<ReleaseItem> getReleaseItemList(final long functionCatalogId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<List<ReleaseItem>, Connection>() {
            @Override
            public List<ReleaseItem> call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final ReleaseDatabaseManager releaseDatabaseManager = new ReleaseDatabaseManager(databaseConnection);
                return releaseDatabaseManager.getReleaseItemList(functionCatalogId);
            }
        });
    }

    public void releaseFunctionCatalog(final long functionCatalogId, final List<ReleaseItem> releaseItems) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
            final ReleaseDatabaseManager releaseDatabaseManager = new ReleaseDatabaseManager(databaseConnection);
            releaseDatabaseManager.releaseFunctionCatalog(functionCatalogId, releaseItems);
            }
        });
    }

    public boolean isNewReleaseVersionUnique(final String itemType, final long itemId, final String proposedReleaseVersion) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Boolean, Connection>() {
            @Override
            public Boolean call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final ReleaseDatabaseManager releaseDatabaseManager = new ReleaseDatabaseManager(databaseConnection);
                return releaseDatabaseManager.isNewReleaseVersionUnique(itemType, itemId, proposedReleaseVersion);
            }
        });
    }

    public List<String> listFunctionIdsAssociatedWithFunctionCatalog(final long functionCatalogId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<List<String>, Connection>() {
            @Override
            public List<String> call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(databaseConnection);
                return functionCatalogDatabaseManager.listAssociatedFunctionIds(functionCatalogId);
            }
        });
    }

    // FUNCTION BLOCK METHODS

    public void insertFunctionBlock(final Long functionCatalogId, final FunctionBlock functionBlock, final AccountId accountId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.insertFunctionBlockForFunctionCatalog(functionCatalogId, functionBlock, accountId);
            }
        });
    }

    public void insertOrphanedFunctionBlock(final FunctionBlock functionBlock) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.insertOrphanedFunctionBlock(functionBlock);
            }
        });
    }

    public void associateFunctionBlockWithFunctionCatalog(final long functionCatalogId, final long functionBlockId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.associateFunctionBlockWithFunctionCatalog(functionCatalogId, functionBlockId);
            }
        });
    }

    public void disassociateFunctionBlockFromFunctionCatalog(final long functionCatalogId, final long functionBlockId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.disassociateFunctionBlockFromFunctionCatalog(functionCatalogId, functionBlockId);
            }
        });
    }

    public void updateFunctionBlock(final FunctionBlock functionBlock, final AccountId currentAccountId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.updateFunctionBlock(functionBlock, currentAccountId);
            }
        });
    }

    public long forkFunctionBlock(final long functionBlockId, final Long parentFunctionCatalogId, final AccountId currentAccountId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Long, Connection>() {
            @Override
            public Long call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                return functionBlockDatabaseManager.forkFunctionBlock(functionBlockId, parentFunctionCatalogId, currentAccountId);
            }
        });
    }

    public boolean functionBlockHasApprovedParents(final long functionBlockId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Boolean, Connection>() {
            @Override
            public Boolean call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                return functionBlockDatabaseManager.hasApprovedParents(functionBlockId);
            }
        });
    }

    public void markFunctionBlockAsDeleted(final long functionBlockId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.setIsDeletedForFunctionBlock(functionBlockId, true);
            }
        });
    }

    public long restoreFunctionBlockFromTrash(final long functionBlockId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Long, Connection>() {
            @Override
            public Long call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                return functionBlockDatabaseManager.restoreFunctionBlockFromTrash(functionBlockId);
            }
        });
    }

    public void deleteFunctionBlock(final long functionBlockId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.deleteFunctionBlock(functionBlockId);
            }
        });
    }

    public List<Long> listFunctionCatalogsContainingFunctionBlock(final long functionBlockId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<List<Long>, Connection>() {
            @Override
            public List<Long> call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                return functionBlockDatabaseManager.listFunctionCatalogIdsContainingFunctionBlock(functionBlockId);
            }
        });
    }

    public void submitFunctionBlockForReview(final long functionBlockId, final AccountId accountId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                functionBlockDatabaseManager.submitFunctionBlockForReview(functionBlockId, accountId);
            }
        });
    }

    public FunctionBlock checkForDuplicateFunctionBlockName(final String functionBlockName, final Long functionBlockVersionSeries) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<FunctionBlock, Connection>() {
            @Override
            public FunctionBlock call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                return functionBlockDatabaseManager.checkForDuplicateFunctionBlockName(functionBlockName, functionBlockVersionSeries);
            }
        });
    }

    public FunctionBlock checkForDuplicateFunctionBlockMostId(final String functionBlockMostId, final Long functionBlockVersionSeriesId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<FunctionBlock, Connection>() {
            @Override
            public FunctionBlock call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                return functionBlockDatabaseManager.checkForDuplicateFunctionBlockMostId(functionBlockMostId, functionBlockVersionSeriesId);
            }
        });
    }

    public List<MostFunction> listFunctionsAssociatedWithFunctionBlock(final long functionBlockId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<List<MostFunction>, Connection>() {
            @Override
            public List<MostFunction> call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                return functionBlockDatabaseManager.listAssociatedFunctions(functionBlockId);
            }
        });
    }

    public boolean functionBlockHasDeletedChildren(final long functionBlockId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Boolean, Connection>() {
            @Override
            public Boolean call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(databaseConnection);
                return functionBlockDatabaseManager.hasDeletedChildren(functionBlockId);
            }
        });
    }

    // MOST INTERFACE METHODS

    public void insertMostInterface(final Long functionBlockId, final MostInterface mostInterface) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.insertMostInterfaceForFunctionBlock(functionBlockId, mostInterface);
            }
        });
    }

    public void insertOrphanedMostInterface(final MostInterface mostInterface) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.insertOrphanedMostInterface(mostInterface);
            }
        });
    }

    public void associateMostInterfaceWithFunctionBlock(final long functionBlockId, final long mostInterfaceId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.associateMostInterfaceWithFunctionBlock(functionBlockId, mostInterfaceId);
            }
        });
    }

    public void disassociateMostInterfaceFromFunctionBlock(final long functionBlockId, final long mostInterfaceId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.disassociateMostInterfaceFromFunctionBlock(functionBlockId, mostInterfaceId);
            }
        });
    }

    public void updateMostInterface(final MostInterface mostInterface, final AccountId currentAccountID) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.updateMostInterface(mostInterface, currentAccountID);
            }
        });
    }

    public long forkMostInterface(final long mostInterfaceId, final Long parentFunctionBlockId, final AccountId currentAccountId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Long, Connection>() {
            @Override
            public Long call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                return mostInterfaceDatabaseManager.forkMostInterface(mostInterfaceId, parentFunctionBlockId, currentAccountId);
            }
        });
    }

    public boolean mostInterfaceHasApprovedParents(final long mostInterfaceId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Boolean, Connection>() {
            @Override
            public Boolean call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                return mostInterfaceDatabaseManager.hasApprovedParents(mostInterfaceId);
            }
        });
    }

    public void markMostInterfaceAsDeleted(final long mostInterfaceId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.setIsDeletedForMostInterface(mostInterfaceId, true);
            }
        });
    }

    public void restoreMostInterfaceFromTrash(final long mostInterfaceId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.restoreMostInterfaceFromTrash(mostInterfaceId);
            }
        });
    }

    public void deleteMostInterface(final long mostInterfaceId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.deleteMostInterface(mostInterfaceId);
            }
        });
    }

    public List<Long> listFunctionBlocksContainingMostInterface(final long mostInterfaceId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<List<Long>, Connection>() {
            @Override
            public List<Long> call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                return mostInterfaceDatabaseManager.listFunctionBlocksContainingMostInterface(mostInterfaceId);
            }
        });
    }

    public void submitMostInterfaceForReview(final long mostInterfaceId, final AccountId submittingAccountId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                mostInterfaceDatabaseManager.submitMostInterfaceForReview(mostInterfaceId, submittingAccountId);
            }
        });
    }

    public MostInterface checkForDuplicateMostInterfaceName(final String mostInterfaceName, final Long mostInterfaceVersionSeriesId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<MostInterface, Connection>() {
            @Override
            public MostInterface call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                return mostInterfaceDatabaseManager.checkForDuplicateMostInterfaceName(mostInterfaceName, mostInterfaceVersionSeriesId);
            }
        });
    }

    public MostInterface checkForDuplicateMostInterfaceMostId(final String mostInterfaceMostId, final Long mostInterfaceVersionSeriesId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<MostInterface, Connection>() {
            @Override
            public MostInterface call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                return mostInterfaceDatabaseManager.checkForDuplicateMostInterfaceMostId(mostInterfaceMostId, mostInterfaceVersionSeriesId);
            }
        });
    }

    public List<MostFunction> listFunctionsAssociatedWithMostInterface(final long mostInterfaceId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<List<MostFunction>, Connection>() {
            @Override
            public List<MostFunction> call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                return mostInterfaceDatabaseManager.listAssociatedFunctions(mostInterfaceId);
            }
        });
    }

    public boolean mostInterfaceHasDeletedChildren(final long mostInterfaceId) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Boolean, Connection>() {
            @Override
            public Boolean call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(databaseConnection);
                return mostInterfaceDatabaseManager.hasDeletedChildren(mostInterfaceId);
            }
        });
    }

    // MOST FUNCTION METHODS

    public void insertMostFunction(final Long mostInterfaceId, final MostFunction mostFunction) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                MostFunctionDatabaseManager mostFunctionDatabaseManager = new MostFunctionDatabaseManager(databaseConnection);
                mostFunctionDatabaseManager.insertMostFunctionForMostInterface(mostInterfaceId, mostFunction);
            }
        });
    }

    public void updateMostFunction(final long mostInterfaceId, final MostFunction mostFunction) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostFunctionDatabaseManager mostFunctionDatabaseManager = new MostFunctionDatabaseManager(databaseConnection);
                mostFunctionDatabaseManager.updateMostFunctionForMostInterface(mostInterfaceId, mostFunction);
            }
        });
    }

    public void markMostFunctionAsDeleted(final long mostFunctionId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostFunctionDatabaseManager mostFunctionDatabaseManager = new MostFunctionDatabaseManager(databaseConnection);
                mostFunctionDatabaseManager.setIsDeletedForMostFunction(mostFunctionId, true);
            }
        });
    }

    public void restoreMostFunctionFromTrash(final long mostFunctionId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostFunctionDatabaseManager mostFunctionDatabaseManager = new MostFunctionDatabaseManager(databaseConnection);
                mostFunctionDatabaseManager.restoreMostFunctionFromTrash(mostFunctionId);
            }
        });
    }

    public void deleteMostFunction(final long mostInterfaceId, final long mostFunctionId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostFunctionDatabaseManager mostFunctionDatabaseManager = new MostFunctionDatabaseManager(databaseConnection);
                mostFunctionDatabaseManager.deleteMostFunction(mostInterfaceId, mostFunctionId);
            }
        });
    }

    // MOST TYPE METHODS

    public void insertMostType(final MostType mostType) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostTypeDatabaseManager mostTypeDatabaseManager = new MostTypeDatabaseManager(databaseConnection);
                mostTypeDatabaseManager.insertMostType(mostType);
            }
        });
    }

    public boolean isMostTypeNameUnique(final MostType mostType) throws DatabaseException {
        return this._executeTransaction(new DatabaseCallable<Boolean, Connection>() {
            @Override
            public Boolean call(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostTypeDatabaseManager mostTypeDatabaseManager = new MostTypeDatabaseManager(databaseConnection);
                return mostTypeDatabaseManager.isMostTypeNameUnique(mostType);
            }
        });
    }

    public void updateMostType(final MostType mostType) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final MostTypeDatabaseManager mostTypeDatabaseManager = new MostTypeDatabaseManager(databaseConnection);
                mostTypeDatabaseManager.updateMostType(mostType);
            }
        });
    }

    // REVIEW METHODS
    public void insertReview(final Review review) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final ReviewDatabaseManager reviewDatabaseManager = new ReviewDatabaseManager(databaseConnection);
                reviewDatabaseManager.insertReview(review);
            }
        });
    }

    public void updateReview(final Review review) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final ReviewDatabaseManager reviewDatabaseManager = new ReviewDatabaseManager(databaseConnection);
                reviewDatabaseManager.updateReview(review);
            }
        });
    }

    public void approveReview(final Review review) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final ReviewDatabaseManager reviewDatabaseManager = new ReviewDatabaseManager(databaseConnection);
                reviewDatabaseManager.approveReview(review);
            }
        });
    }

    public void insertReviewVote(final ReviewVote reviewVote, final long reviewId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final ReviewDatabaseManager reviewDatabaseManager = new ReviewDatabaseManager(databaseConnection);
                reviewDatabaseManager.insertReviewVote(reviewVote, reviewId);
            }
        });
    }

    public void updateReviewVote(final ReviewVote reviewVote) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final ReviewDatabaseManager reviewDatabaseManager = new ReviewDatabaseManager(databaseConnection);
                reviewDatabaseManager.updateReviewVote(reviewVote);
            }
        });
    }

    public void deleteReviewVote(final long reviewVoteId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final ReviewDatabaseManager reviewDatabaseManager = new ReviewDatabaseManager(databaseConnection);
                reviewDatabaseManager.deleteReviewVote(reviewVoteId);
            }
        });
    }

    public void insertReviewComment(final ReviewComment reviewComment, final long reviewId) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final ReviewDatabaseManager reviewDatabaseManager = new ReviewDatabaseManager(databaseConnection);
                reviewDatabaseManager.insertReviewComment(reviewComment, reviewId);
            }
        });
    }

    public void updateAccountRoles(final AccountId accountId, final List<Role> roles) throws DatabaseException {
        this._executeTransaction(new DatabaseRunnable<Connection>() {
            @Override
            public void run(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
                final AccountDatabaseManager accountDatabaseManager = new AccountDatabaseManager(databaseConnection);
                accountDatabaseManager.updateAccountRoles(accountId, roles);
            }
        });
    }
}
