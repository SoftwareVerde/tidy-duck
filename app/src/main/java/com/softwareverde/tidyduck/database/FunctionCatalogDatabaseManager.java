package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.database.row.Row;
import com.softwareverde.tidyduck.AccountId;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.Review;
import com.softwareverde.tidyduck.most.FunctionBlock;
import com.softwareverde.tidyduck.most.FunctionCatalog;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class FunctionCatalogDatabaseManager {
    private final DatabaseConnection<Connection> _databaseConnection;

    public FunctionCatalogDatabaseManager(final DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    /**
     * Stores the functionCatalog's release, releaseDate, accountId, and companyId via the databaseConnection.
     * Upon successful insert, the functionCatalog's Id is set to the database's insertId.
     */
    private void _insertFunctionCatalog(final FunctionCatalog functionCatalog) throws DatabaseException {
        final String name = functionCatalog.getName();
        final String release = functionCatalog.getRelease();
        final AccountId accountId = functionCatalog.getAuthor().getId();
        final Long companyId = functionCatalog.getCompany().getId();
        final AccountId creatorAccountId = functionCatalog.getCreatorAccountId();

        final Query query = new Query("INSERT INTO function_catalogs (name, release_version, account_id, company_id, prior_version_id, creator_account_id) VALUES (?, ?, ?, ?, NULL, ?)")
            .setParameter(name)
            .setParameter(release)
            .setParameter(accountId)
            .setParameter(companyId)
            .setParameter(creatorAccountId)
        ;

        final long functionCatalogId = _databaseConnection.executeSql(query);
        functionCatalog.setId(functionCatalogId);
        _setBaseVersionId(functionCatalogId, functionCatalogId);
    }

    private long _forkFunctionCatalog(final FunctionCatalog functionCatalog, final AccountId creatorAccountId) throws DatabaseException {
        final String name = functionCatalog.getName();
        final String release = functionCatalog.getRelease();
        final AccountId accountId = functionCatalog.getAuthor().getId();
        final Long companyId = functionCatalog.getCompany().getId();
        final Long priorVersionId = functionCatalog.getId();
        final Long baseVersionId = functionCatalog.getBaseVersionId();

        final Query query = new Query("INSERT INTO function_catalogs (name, release_version, account_id, company_id, prior_version_id, creator_account_id, base_version_id) VALUES (?, ?, ?, ?, ?, ?, ?)")
                .setParameter(name)
                .setParameter(release)
                .setParameter(accountId)
                .setParameter(companyId)
                .setParameter(priorVersionId)
                .setParameter(creatorAccountId)
                .setParameter(baseVersionId)
                ;

        final long newFunctionCatalogId = _databaseConnection.executeSql(query);
        return newFunctionCatalogId;
    }

    private void _setBaseVersionId(long functionCatalogId, long baseVersionId) throws DatabaseException {
        final Query query = new Query("UPDATE function_catalogs SET base_version_id = ? WHERE id = ?")
            .setParameter(baseVersionId)
            .setParameter(functionCatalogId)
        ;

        _databaseConnection.executeSql(query);
    }

    private void _updateFunctionCatalog(final FunctionCatalog proposedFunctionCatalog) throws DatabaseException {
        final String newName = proposedFunctionCatalog.getName();
        final String newReleaseVersion = proposedFunctionCatalog.getRelease();
        final AccountId newAuthorId = proposedFunctionCatalog.getAuthor().getId();
        final long newCompanyId = proposedFunctionCatalog.getCompany().getId();
        final long functionCatalogId = proposedFunctionCatalog.getId();
        final AccountId creatorAccountId = proposedFunctionCatalog.getCreatorAccountId();

        final Query query = new Query("UPDATE function_catalogs SET name = ?, release_version = ?, account_id = ?, company_id = ?, is_approved = ?, creator_account_id = ? WHERE id = ?")
            .setParameter(newName)
            .setParameter(newReleaseVersion)
            .setParameter(newAuthorId)
            .setParameter(newCompanyId)
            .setParameter(false)
            .setParameter(creatorAccountId)
            .setParameter(functionCatalogId)
        ;

        _databaseConnection.executeSql(query);
    }

    public void setIsDeletedForFunctionCatalog(final long functionCatalogId, final boolean isDeleted) throws DatabaseException {
        final Query query = new Query("UPDATE function_catalogs SET is_deleted = ?, deleted_date = ? WHERE id = ?")
                .setParameter(isDeleted)
                .setParameter(isDeleted ? DateUtil.dateToDateString(new Date()) : null)
                .setParameter(functionCatalogId)
        ;

        _databaseConnection.executeSql(query);
    }
    
    public long restoreFunctionCatalogFromTrash(final long functionCatalogId) throws DatabaseException {
        setIsDeletedForFunctionCatalog(functionCatalogId, false);
        final long numberOfDeletedChildren = _getNumberOfDeletedChildren();

        if (numberOfDeletedChildren > 0) {
            _clearDeletedChildAssociations();
        }

        return numberOfDeletedChildren;
    }

    private long _getNumberOfDeletedChildren() throws DatabaseException {
        final Query query = new Query("SELECT COUNT(*) AS deletions FROM function_catalogs_function_blocks WHERE function_block_id IS NULL");

        final List<Row> rows = _databaseConnection.query(query);
        final Row row = rows.get(0);
        return row.getLong("deletions");
    }

    private void _clearDeletedChildAssociations() throws DatabaseException {
        final Query query = new Query("DELETE FROM function_catalogs_function_blocks WHERE function_block_id IS NULL");
        _databaseConnection.executeSql(query);

    }

    private void _deleteFunctionCatalog(final long functionCatalogId) throws DatabaseException {
        final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(_databaseConnection);
        final FunctionCatalog functionCatalog = functionCatalogInflater.inflateFunctionCatalog(functionCatalogId);

        if (functionCatalog.isReleased()) {
            throw new IllegalStateException("Released function catalogs cannot be deleted.");
        }

        if (!functionCatalog.isDeleted()) {
            throw new IllegalStateException("Only trashed items can be deleted.");
        }

        if (functionCatalog.isApproved()) {
            // approved, be careful
            _markAsPermanentlyDeleted(functionCatalogId);
        }
        else {
            // not approved, delete
            _deleteFunctionBlocksFromFunctionCatalog(functionCatalogId);
            _deleteReviewForFunctionCatalog(functionCatalogId);
            _deleteFunctionCatalogFromDatabase(functionCatalogId);
        }
    }

    private void _markAsPermanentlyDeleted(final long functionCatalogId) throws DatabaseException {
        final Query query = new Query("UPDATE function_catalogs SET is_permanently_deleted = 1, permanently_deleted_date = NOW() WHERE id = ?")
                .setParameter(functionCatalogId)
                ;

        _databaseConnection.executeSql(query);
    }

    private void _deleteFunctionBlocksFromFunctionCatalog(final long functionCatalogId) throws DatabaseException {
        final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
        final List<FunctionBlock> functionBlocks = functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalogId);

        final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(_databaseConnection);
        for (final FunctionBlock functionBlock : functionBlocks) {
            functionBlockDatabaseManager.disassociateFunctionBlockFromFunctionCatalog(functionCatalogId, functionBlock.getId());
        }
    }

    private void _deleteFunctionCatalogFromDatabase(final long functionCatalogId) throws DatabaseException {
        final Query query = new Query("DELETE FROM function_catalogs WHERE id = ?")
            .setParameter(functionCatalogId)
        ;

        _databaseConnection.executeSql(query);
    }

    public void insertFunctionCatalog(final FunctionCatalog functionCatalog) throws DatabaseException {
        _insertFunctionCatalog(functionCatalog);
    }

    public void updateFunctionCatalog(final FunctionCatalog functionCatalog, final AccountId accountId) throws DatabaseException {
        _updateFunctionCatalog(functionCatalog);
    }

    public long forkFunctionCatalog(final long functionCatalogId, final AccountId accountId) throws DatabaseException {
        final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(_databaseConnection);
        final FunctionCatalog functionCatalog = functionCatalogInflater.inflateFunctionCatalog(functionCatalogId);
        // need to insert a new function catalog replace this one
        final long newFunctionCatalogId = _forkFunctionCatalog(functionCatalog, accountId);
        _copyFunctionCatalogFunctionBlocksAssociations(functionCatalogId, newFunctionCatalogId);
        return newFunctionCatalogId;
    }

    public void deleteFunctionCatalog(final long functionCatalogId) throws DatabaseException {
        _deleteFunctionCatalog(functionCatalogId);
    }

    private void _copyFunctionCatalogFunctionBlocksAssociations(final long originalFunctionCatalogId, final long newFunctionCatalogId) throws DatabaseException {
        final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
        final List<FunctionBlock> functionBlocks = functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(originalFunctionCatalogId);

        final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(_databaseConnection);
        for (final FunctionBlock functionBlock : functionBlocks) {
            functionBlockDatabaseManager.associateFunctionBlockWithFunctionCatalog(newFunctionCatalogId, functionBlock.getId());
        }
    }

    public void submitFunctionCatalogForReview(final Long functionCatalogId, final AccountId accountId) throws DatabaseException {
        if (_functionCatalogHasReview(functionCatalogId)) {
            // already present, return
            return;
        }
        _submitFunctionCatalogForReview(functionCatalogId, accountId);
    }

    private boolean _functionCatalogHasReview(final long functionCatalogId) throws DatabaseException {
        final Query query = new Query("SELECT * FROM reviews WHERE function_catalog_id = ?");
        query.setParameter(functionCatalogId);

        List<Row> rows = _databaseConnection.query(query);
        return rows.size() > 0;
    }

    private void _submitFunctionCatalogForReview(final Long functionCatalogId, final AccountId accountId) throws DatabaseException {
        final Query query = new Query("INSERT INTO reviews (function_catalog_id, account_id, created_date) VALUES (?, ?, NOW())");
        query.setParameter(functionCatalogId);
        query.setParameter(accountId);

        final long newReviewId = _databaseConnection.executeSql(query);
        _setReviewIdForFunctionCatalogId(functionCatalogId, newReviewId);
    }

    private void _setReviewIdForFunctionCatalogId(final Long functionCatalogId, final Long reviewId) throws DatabaseException {
        final Query query = new Query("UPDATE function_catalogs SET approval_review_id = COALESCE(approval_review_id, ?) WHERE id = ?")
                .setParameter(reviewId)
                .setParameter(functionCatalogId)
        ;

        _databaseConnection.executeSql(query);
    }

    public void approveFunctionCatalog(final long functionCatalogId, final long reviewId) throws DatabaseException {
        final Query query = new Query("UPDATE function_catalogs SET is_approved = ?, approval_review_id = COALESCE(approval_review_id, ?) WHERE id = ?")
                .setParameter(true)
                .setParameter(reviewId)
                .setParameter(functionCatalogId);

        _databaseConnection.executeSql(query);

        _approveFunctionBlocksForFunctionCatalogId(functionCatalogId, reviewId);
    }

    private void _approveFunctionBlocksForFunctionCatalogId(final long functionCatalogId, final long reviewId) throws DatabaseException {
        final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
        final List<FunctionBlock> functionBlocks = functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalogId);

        final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(_databaseConnection);
        for (final FunctionBlock functionBlock : functionBlocks) {
            if (! functionBlock.isApproved()) {
                functionBlockDatabaseManager.approveFunctionBlock(functionBlock.getId(), reviewId);
            }
        }
    }

    private void _deleteReviewForFunctionCatalog(final long functionCatalogId) throws DatabaseException {
        final Query query = new Query("SELECT * FROM reviews WHERE function_catalog_id = ?")
                .setParameter(functionCatalogId);

        final List<Row> rows = _databaseConnection.query(query);
        final List<Review> reviews = new ArrayList<>();

        // Inflate reviews
        final ReviewInflater reviewInflater = new ReviewInflater(_databaseConnection);
        for (Row row : rows) {
            final Review review = reviewInflater._convertRowToReview(row);
            reviews.add(review);
        }

        final ReviewDatabaseManager reviewDatabaseManager = new ReviewDatabaseManager(_databaseConnection);
        for (Review review: reviews) {
            reviewDatabaseManager.deleteReview(review);
        }
    }

    public boolean isApproved(final Long functionCatalogId) throws DatabaseException {
        return _isApproved(functionCatalogId);
    }

    private boolean _isApproved(final Long functionCatalogId) throws DatabaseException {
        final Query query = new Query("SELECT is_approved FROM function_catalogs WHERE id = ?");
        query.setParameter(functionCatalogId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() > 0) {
            return rows.get(0).getBoolean("is_approved");
        }
        return false;
    }

    public FunctionCatalog checkForDuplicateFunctionCatalog(final String functionCatalogName, final Long functionCatalogVersionSeries) throws DatabaseException {
        return _checkForDuplicateFunctionCatalog(functionCatalogName, functionCatalogVersionSeries);
    }

    private FunctionCatalog _checkForDuplicateFunctionCatalog(final String functionCatalogName, final Long functionCatalogVersionSeries) throws DatabaseException {
        final Query query = new Query("SELECT id FROM function_catalogs WHERE name = ?");
        query.setParameter(functionCatalogName);

        final List<Row> rows = _databaseConnection.query(query);
        final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(_databaseConnection);

        FunctionCatalog matchedFunctionCatalog = null;
        for (final Row row : rows) {
            final long functionCatalogId = row.getLong("id");
            final FunctionCatalog rowFunctionCatalog = functionCatalogInflater.inflateFunctionCatalog(functionCatalogId);

            if (!rowFunctionCatalog.getBaseVersionId().equals(functionCatalogVersionSeries)) {
                matchedFunctionCatalog = rowFunctionCatalog;
                break;
            }
        }

        return matchedFunctionCatalog;
    }
    
    public List<String> listAssociatedFunctionIds(final long functionCatalogId) throws DatabaseException {
        return _getAssociatedFunctionIds(functionCatalogId);
    }

    private List<String> _getAssociatedFunctionIds(final long functionCatalogId) throws DatabaseException {
        final List<String> functionIds = new ArrayList<>();

        final Query query = new Query("SELECT functions.most_id FROM functions INNER JOIN interfaces_functions ON functions.id = interfaces_functions.function_id INNER JOIN function_blocks_interfaces ON function_blocks_interfaces.interface_id = interfaces_functions.interface_id INNER JOIN function_catalogs_function_blocks ON function_catalogs_function_blocks.function_block_id = function_blocks_interfaces.function_block_id WHERE function_catalog_id = ?");
        query.setParameter(functionCatalogId);

        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            functionIds.add(row.getString("most_id"));
        }

        return functionIds;
    }

    public boolean hasDeletedChildren(final long functionCatalogId) throws DatabaseException {
        final Query query = new Query("SELECT 1 " +
                                      "FROM function_catalogs_function_blocks " +
                                      "LEFT OUTER JOIN function_blocks_interfaces ON function_catalogs_function_blocks.function_block_id = function_blocks_interfaces.function_block_id " +
                                      "LEFT OUTER JOIN interfaces_functions ON function_blocks_interfaces.interface_id = interfaces_functions.interface_id " +
                                      "WHERE function_catalog_id = ? AND (function_catalogs_function_blocks.is_deleted = 1 OR function_blocks_interfaces.is_deleted = 1 OR interfaces_functions.is_deleted = 1)")
                .setParameter(functionCatalogId);

        List<Row> rows = _databaseConnection.query(query);
        return rows.size() != 0;
    }
}
