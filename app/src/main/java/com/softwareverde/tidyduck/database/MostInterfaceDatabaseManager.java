package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.database.row.Row;
import com.softwareverde.tidyduck.AccountId;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.Review;
import com.softwareverde.tidyduck.most.MostInterface;
import com.softwareverde.tidyduck.most.MostFunction;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MostInterfaceDatabaseManager {
    private final DatabaseConnection<Connection> _databaseConnection;

    private void _insertMostInterface(final MostInterface mostInterface, final MostInterface priorMostInterface) throws DatabaseException {
        final String mostId = mostInterface.getMostId();
        final String name = mostInterface.getName();
        final String description = mostInterface.getDescription();
        final String version = mostInterface.getVersion();
        final Long priorVersionId = priorMostInterface != null ? priorMostInterface.getId() : null;
        final AccountId creatorAccountId = mostInterface.getCreatorAccountId();

        final Query query = new Query("INSERT INTO interfaces (most_id, name, description, last_modified_date, version, prior_version_id, creator_account_id) VALUES (?, ?, ?, NOW(), ?, ?, ?)")
            .setParameter(mostId)
            .setParameter(name)
            .setParameter(description)
            .setParameter(version)
            .setParameter(priorVersionId)
            .setParameter(creatorAccountId)
        ;

        final long mostInterfaceId = _databaseConnection.executeSql(query);
        mostInterface.setId(mostInterfaceId);

        if (priorMostInterface == null) {
            _setBaseVersionId(mostInterfaceId, mostInterfaceId);
        }
        else {
            _setBaseVersionId(mostInterfaceId, priorMostInterface.getBaseVersionId());
        }
    }

    private void _insertMostInterface(final MostInterface mostInterface) throws DatabaseException {
        _insertMostInterface(mostInterface, null);
    }

    private long _forkMostInterface(final MostInterface mostInterface) throws DatabaseException {
        final String mostId = mostInterface.getMostId();
        final String name = mostInterface.getName();
        final String description = mostInterface.getDescription();
        final String version = mostInterface.getVersion();
        final Long priorVersionId = mostInterface.getId();
        final AccountId creatorAccountId = mostInterface.getCreatorAccountId();
        final Long baseVersionId = mostInterface.getBaseVersionId();

        final Query query = new Query("INSERT INTO interfaces (most_id, name, description, last_modified_date, version, prior_version_id, creator_account_id, base_version_id) VALUES (?, ?, ?, NOW(), ?, ?, ?, ?)")
                .setParameter(mostId)
                .setParameter(name)
                .setParameter(description)
                .setParameter(version)
                .setParameter(priorVersionId)
                .setParameter(creatorAccountId)
                .setParameter(baseVersionId)
                ;

        final long newMostInterfaceId = _databaseConnection.executeSql(query);
        return newMostInterfaceId;

    }

    private void _setBaseVersionId(long mostInterfaceId, long baseVersionId) throws DatabaseException {
        final Query query = new Query("UPDATE interfaces SET base_version_id = ? WHERE id = ?")
            .setParameter(baseVersionId)
            .setParameter(mostInterfaceId)
        ;

        _databaseConnection.executeSql(query);
    }

    private void _setCreatorAccountId(long mostInterfaceId, AccountId accountId) throws DatabaseException {
        final Query query = new Query("UPDATE interfaces SET creator_account_id = ? WHERE id = ?")
                .setParameter(accountId)
                .setParameter(mostInterfaceId)
                ;

        _databaseConnection.executeSql(query);
    }

    private Long _associateMostInterfaceWithFunctionBlock(final long functionBlockId, final long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("INSERT INTO function_blocks_interfaces (function_block_id, interface_id) VALUES (?, ?)")
            .setParameter(functionBlockId)
            .setParameter(mostInterfaceId)
        ;

        return _databaseConnection.executeSql(query);
    }

    private boolean _isAssociatedWithFunctionBlock(final long functionBlockId, final long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("SELECT id FROM function_blocks_interfaces WHERE function_block_id = ? AND interface_id = ?")
            .setParameter(functionBlockId)
            .setParameter(mostInterfaceId)
        ;

        List<Row> rows = _databaseConnection.query(query);

        return rows.size() > 0;
    }

    private void _updateUnapprovedMostInterface(MostInterface proposedMostInterface) throws DatabaseException {
        final String newMostId = proposedMostInterface.getMostId();
        final String newName = proposedMostInterface.getName();
        final String newVersion = proposedMostInterface.getVersion();
        final String newDescription = proposedMostInterface.getDescription();
        final long mostInterfaceId = proposedMostInterface.getId();
        final AccountId creatorAccountId = proposedMostInterface.getCreatorAccountId();

        final Query query = new Query("UPDATE interfaces SET most_id = ?, name = ?, description = ?, last_modified_date = NOW(), version = ?, is_approved = ?, creator_account_id = ? WHERE id = ?")
            .setParameter(newMostId)
            .setParameter(newName)
            .setParameter(newDescription)
            .setParameter(newVersion)
            .setParameter(false)
            .setParameter(creatorAccountId)
            .setParameter(mostInterfaceId)
        ;

        _databaseConnection.executeSql(query);
    }

    private void _disassociateMostInterfaceWithFunctionBlock(long functionBlockId, long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("DELETE FROM function_blocks_interfaces WHERE function_block_id = ? and interface_id = ?")
            .setParameter(functionBlockId)
            .setParameter(mostInterfaceId)
        ;

        _databaseConnection.executeSql(query);
    }
    private void _disassociateMostInterfaceFromAllUnReleasedFunctionBlocks(final long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("DELETE FROM function_blocks_interfaces WHERE interface_id = ? and function_block_id IN (" +
                "SELECT DISTINCT function_blocks.id\n" +
                    "FROM function_blocks\n" +
                    "WHERE function_blocks.is_approved=0)")
                .setParameter(mostInterfaceId);

        _databaseConnection.executeSql(query);
    }

    public void setIsDeletedForMostInterface(final long mostInterfaceId, final boolean isDeleted) throws DatabaseException {
        final Query query = new Query("UPDATE interfaces SET is_deleted = ?, deleted_date = ? WHERE id=?")
                .setParameter(isDeleted)
                .setParameter(isDeleted ? DateUtil.dateToDateString(new Date()) : null)
                .setParameter(mostInterfaceId)
                ;

        _databaseConnection.executeSql(query);

        _setIsDeletedForMostInterfaceParentAssociations(mostInterfaceId, isDeleted);
    }

    private void _setIsDeletedForMostInterfaceParentAssociations(final long mostInterfaceId, final boolean isDeleted) throws DatabaseException {
        final Query query = new Query("UPDATE function_blocks_interfaces SET is_deleted = ? WHERE interface_id = ?")
                .setParameter(isDeleted)
                .setParameter(mostInterfaceId)
                ;

        _databaseConnection.executeSql(query);
    }

    public void restoreMostInterfaceFromTrash(final long mostInterfaceId) throws DatabaseException {
        setIsDeletedForMostInterface(mostInterfaceId, false);
    }

    private void _nullifyMostInterfaceParentRelationships(final long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("UPDATE function_blocks_interfaces SET interface_id = NULL WHERE interface_id = ?")
            .setParameter(mostInterfaceId)
        ;

        _databaseConnection.executeSql(query);
    }

    private void _deleteMostInterface(final long mostInterfaceId) throws DatabaseException {
        MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);
        MostInterface mostInterface = mostInterfaceInflater.inflateMostInterface(mostInterfaceId);

        if (mostInterface.isReleased()) {
            throw new IllegalStateException("Released function catalogs cannot be deleted.");
        }

        if (!mostInterface.isDeleted()) {
            throw new IllegalStateException("Only trashed items can be deleted.");
        }

        if (mostInterface.isApproved()) {
            // approved, be careful
            _markAsPermanentlyDeleted(mostInterfaceId);
            _deleteMostFunctionsFromMostInterface(mostInterfaceId);
        }
        else {
            // not approved, delete
            _deleteMostFunctionsFromMostInterface(mostInterfaceId);
            _disassociateMostInterfaceFromAllUnReleasedFunctionBlocks(mostInterfaceId);
            _deleteReviewForMostInterface(mostInterfaceId);
            _deleteMostInterfaceFromDatabase(mostInterfaceId);
        }
    }

    private void _markAsPermanentlyDeleted(final long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("UPDATE interfaces SET is_permanently_deleted = 1, permanently_deleted_date = NOW() WHERE id = ?")
                .setParameter(mostInterfaceId)
                ;

        _databaseConnection.executeSql(query);
    }

    private void _deleteMostFunctionsFromMostInterface(final long mostInterfaceId) throws DatabaseException {
        final MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(_databaseConnection);
        final List<MostFunction> mostFunctions = mostFunctionInflater.inflateMostFunctionsFromMostInterfaceId(mostInterfaceId, true);

        final MostFunctionDatabaseManager mostFunctionDatabaseManager = new MostFunctionDatabaseManager(_databaseConnection);
        for (final MostFunction mostFunction : mostFunctions) {
            // trash function so it can be deleted
            mostFunctionDatabaseManager.setIsDeletedForMostFunction(mostFunction.getId(), true);
            // perform normal deletion logic (including approved check, etc.)
            mostFunctionDatabaseManager.deleteMostFunction(mostInterfaceId, mostFunction.getId());
        }
    }

    private void _deleteMostInterfaceFromDatabase(final long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("DELETE FROM interfaces WHERE id = ?")
            .setParameter(mostInterfaceId)
        ;

        _databaseConnection.executeSql(query);
    }

    /**
     * Returns true if and only if the interface with the given idea is not associated with any function blocks.
     * @return
     */
    private boolean isOrphaned(final long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("SELECT COUNT(*) AS associations FROM function_blocks_interfaces WHERE interface_id = ?")
            .setParameter(mostInterfaceId)
        ;

        List<Row> rows = _databaseConnection.query(query);

        Row row = rows.get(0);
        final long associationCount = row.getLong("associations");
        return associationCount == 0;
    }

    public MostInterfaceDatabaseManager(final DatabaseConnection databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public void insertMostInterfaceForFunctionBlock(final long functionBlockId, final MostInterface mostInterface) throws DatabaseException {
        // TODO: check to see whether function block is approved.
        _insertMostInterface(mostInterface);
        _associateMostInterfaceWithFunctionBlock(functionBlockId, mostInterface.getId());
    }

    public void insertOrphanedMostInterface(final MostInterface mostInterface) throws DatabaseException {
        _insertMostInterface(mostInterface);
    }

    public long forkMostInterface(final long mostInterfaceId, final Long parentFunctionBlockId, final AccountId currentAccountId) throws DatabaseException {
        MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);
        MostInterface mostInterface = mostInterfaceInflater.inflateMostInterface(mostInterfaceId);

        final long newMostInterfaceId = _forkMostInterface(mostInterface);
        _copyMostInterfaceMostFunctions(mostInterfaceId, newMostInterfaceId);
        if (parentFunctionBlockId != null) {
            _associateMostInterfaceWithFunctionBlock(parentFunctionBlockId, newMostInterfaceId);
            _disassociateMostInterfaceWithFunctionBlock(parentFunctionBlockId, mostInterfaceId);
        }
        return newMostInterfaceId;
    }

    public void updateMostInterface(final MostInterface proposedMostInterface, final AccountId currentAccountId) throws DatabaseException {
        _updateUnapprovedMostInterface(proposedMostInterface);
    }

    private void _copyMostInterfaceMostFunctions(final long originalMostInterfaceId, final long newMostInterfaceId) throws DatabaseException {
        final MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(_databaseConnection);
        final List<MostFunction> mostFunctions = mostFunctionInflater.inflateMostFunctionsFromMostInterfaceId(originalMostInterfaceId, false);

        final MostFunctionDatabaseManager mostFunctionDatabaseManager = new MostFunctionDatabaseManager(_databaseConnection);
        for (final MostFunction mostFunction : mostFunctions) {
            // Need to insert copy of most function rather than associate existing most function.
            mostFunctionDatabaseManager.insertMostFunctionForMostInterface(newMostInterfaceId, mostFunction);
        }
    }

    public void deleteMostInterface(final long mostInterfaceId) throws DatabaseException {
        _deleteMostInterface(mostInterfaceId);
    }

    public List<Long> listFunctionBlocksContainingMostInterface(final long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("SELECT id FROM function_blocks WHERE id IN (" +
                                        "SELECT DISTINCT function_blocks_interfaces.function_block_id\n" +
                                        "FROM function_blocks_interfaces\n" +
                                        "WHERE function_blocks_interfaces.interface_id = ? AND is_deleted = 0" +
                                      ") AND is_permanently_deleted = 0"
        );
        query.setParameter(mostInterfaceId);

        List<Row> rows =_databaseConnection.query(query);
        final ArrayList<Long> functionBlockIds = new ArrayList<Long>();
        for (Row row : rows) {
            Long functionBlockId = row.getLong("id");
            functionBlockIds.add(functionBlockId);
        }
        return functionBlockIds;
    }

    public Long associateMostInterfaceWithFunctionBlock(final long functionBlockId, final long mostInterfaceId) throws DatabaseException {
        if (!_isAssociatedWithFunctionBlock(functionBlockId, mostInterfaceId)) {
            return _associateMostInterfaceWithFunctionBlock(functionBlockId, mostInterfaceId);
        }
        return null;
    }

    public void disassociateMostInterfaceFromFunctionBlock(final long functionBlockId, final long mostInterfaceId) throws DatabaseException {
        if (_isAssociatedWithFunctionBlock(functionBlockId, mostInterfaceId)) {
            _disassociateMostInterfaceWithFunctionBlock(functionBlockId, mostInterfaceId);
        }
    }

    public void submitMostInterfaceForReview(final long mostInterfaceId, final AccountId submittingAccountId) throws DatabaseException {
        if (_mostInterfaceHasReview(mostInterfaceId)) {
            // already present, return
            return;
        }
        _submitMostInterfaceForReview(mostInterfaceId, submittingAccountId);
    }

    private boolean _mostInterfaceHasReview(final long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("SELECT * FROM reviews WHERE interface_id = ?");
        query.setParameter(mostInterfaceId);

        List<Row> rows = _databaseConnection.query(query);
        return rows.size() > 0;
    }

    private void _submitMostInterfaceForReview(final long mostInterfaceId, final AccountId submittingAccountId) throws DatabaseException {
        final Query query = new Query("INSERT INTO reviews (interface_id, account_id, created_date) VALUES (?, ?, NOW())");
        query.setParameter(mostInterfaceId);
        query.setParameter(submittingAccountId);

        final long newReviewId = _databaseConnection.executeSql(query);
        _setReviewIdForMostInterfaceId(mostInterfaceId, newReviewId);
    }

    private void _setReviewIdForMostInterfaceId(final Long mostInterfaceId, final Long reviewId) throws DatabaseException {
        final Query query = new Query("UPDATE interfaces SET approval_review_id = COALESCE(approval_review_id, ?) WHERE id = ?")
                .setParameter(reviewId)
                .setParameter(mostInterfaceId)
                ;

        _databaseConnection.executeSql(query);
    }

    public void approveMostInterface(final long mostInterfaceId, final long reviewId) throws DatabaseException {
        final Query query = new Query("UPDATE interfaces SET is_approved = ?, approval_review_id = ? WHERE id = ?")
                .setParameter(true)
                .setParameter(reviewId)
                .setParameter(mostInterfaceId);

        _databaseConnection.executeSql(query);

        _approveMostFunctionsForMostInterfaceId(mostInterfaceId, reviewId);
    }

    private void _approveMostFunctionsForMostInterfaceId(final long mostInterfaceId, final long reviewId) throws DatabaseException {
        final MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(_databaseConnection);
        final List<MostFunction> mostFunctions = mostFunctionInflater.inflateMostFunctionsFromMostInterfaceId(mostInterfaceId, false);

        final MostFunctionDatabaseManager mostFunctionDatabaseManager = new MostFunctionDatabaseManager(_databaseConnection);
        for (final MostFunction mostFunction : mostFunctions) {
            mostFunctionDatabaseManager.approveMostFunction(mostFunction.getId(), reviewId);
        }
    }

    private void _deleteReviewForMostInterface(final long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("SELECT * FROM reviews WHERE interface_id = ?")
                .setParameter(mostInterfaceId);

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

    public MostInterface checkForDuplicateMostInterfaceName(final String mostInterfaceName, final Long mostInterfaceVersionSeries) throws DatabaseException {
        return _checkForDuplicateMostInterfaceName(mostInterfaceName, mostInterfaceVersionSeries);
    }

    private MostInterface _checkForDuplicateMostInterfaceName(final String mostInterfaceName, final Long mostInterfaceVersionSeries) throws DatabaseException {
        final Query query = new Query("SELECT id FROM interfaces WHERE name = ?");
        query.setParameter(mostInterfaceName);

        final List<Row> rows = _databaseConnection.query(query);
        final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);

        MostInterface matchedMostInterface = null;
        for (final Row row : rows) {
            final long mostInterfaceId = row.getLong("id");
            final MostInterface rowMostInterface = mostInterfaceInflater.inflateMostInterface(mostInterfaceId);

            if (!rowMostInterface.getBaseVersionId().equals(mostInterfaceVersionSeries)) {
                matchedMostInterface = rowMostInterface;
                break;
            }
        }

        return matchedMostInterface;
    }

    public MostInterface checkForDuplicateMostInterfaceMostId(final String mostInterfaceMostId, final Long mostInterfaceVersionSeriesId) throws DatabaseException {
        return _checkForDuplicateMostInterfaceMostId(mostInterfaceMostId, mostInterfaceVersionSeriesId);
    }

    private MostInterface _checkForDuplicateMostInterfaceMostId(final String mostInterfaceMostId, final Long mostInterfaceVersionSeriesId) throws DatabaseException {
        final Query query = new Query("SELECT id FROM interfaces WHERE most_id = ?");
        query.setParameter(mostInterfaceMostId);

        final List<Row> rows = _databaseConnection.query(query);
        final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);

        MostInterface matchedMostInterface = null;
        for (final Row row : rows) {
            final long mostInterfaceId = row.getLong("id");
            final MostInterface rowMostInterface = mostInterfaceInflater.inflateMostInterface(mostInterfaceId);

            if (!rowMostInterface.getBaseVersionId().equals(mostInterfaceVersionSeriesId)) {
                matchedMostInterface = rowMostInterface;
                break;
            }
        }

        return matchedMostInterface;
    }

    public List<MostFunction> listAssociatedFunctions(final long mostInterfaceId) throws DatabaseException {
        return _getAssociatedFunctions(mostInterfaceId);
    }

    private List<MostFunction> _getAssociatedFunctions(final long mostInterfaceId) throws DatabaseException {
        final List<MostFunction> functions = new ArrayList<>();

        final Query query = new Query("SELECT functions.id FROM functions INNER JOIN interfaces_functions ON functions.id = interfaces_functions.function_id WHERE interface_id = ?");
        query.setParameter(mostInterfaceId);

        final List<Row> rows = _databaseConnection.query(query);
        MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(_databaseConnection);
        for (final Row row : rows) {
            final long mostFunctionId = row.getLong("id");
            final MostFunction mostFunction = mostFunctionInflater.inflateMostFunction(mostFunctionId);
            functions.add(mostFunction);
        }

        return functions;
    }

    public boolean hasApprovedParents(final long mostInterfaceId) throws DatabaseException {
        return _hasApprovedParent(mostInterfaceId);
    }

    private boolean _hasApprovedParent(final long mostInterfaceId) throws DatabaseException {
        // general check for all parents, even those owned by other users
        final Query query = new Query("SELECT 1 FROM function_blocks INNER JOIN function_blocks_interfaces ON function_blocks.id = function_blocks_interfaces.function_block_id WHERE interface_id = ? and is_approved = 1")
                .setParameter(mostInterfaceId)
                ;

        List<Row> rows = _databaseConnection.query(query);
        return rows.size() > 0;
    }

    public boolean hasDeletedChildren(final long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("SELECT 1 " +
                                      "FROM interfaces_functions " +
                                      "WHERE interface_id = ? AND interfaces_functions.is_deleted = 1")
                .setParameter(mostInterfaceId);

        List<Row> rows = _databaseConnection.query(query);
        return rows.size() != 0;
    }
}
