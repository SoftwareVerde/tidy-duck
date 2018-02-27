package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.Review;
import com.softwareverde.tidyduck.most.MostInterface;
import com.softwareverde.tidyduck.most.MostFunction;

import java.util.ArrayList;
import java.util.List;

public class MostInterfaceDatabaseManager {
    private final DatabaseConnection _databaseConnection;

    private void _insertMostInterface(final MostInterface mostInterface, final MostInterface priorMostInterface) throws DatabaseException {
        final String mostId = mostInterface.getMostId();
        final String name = mostInterface.getName();
        final String description = mostInterface.getDescription();
        final String version = mostInterface.getVersion();
        final Long priorVersionId = priorMostInterface != null ? priorMostInterface.getId() : null;
        final Long creatorAccountId = mostInterface.getCreatorAccountId();

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
        final Long creatorAccountId = mostInterface.getCreatorAccountId();
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

    private void _setCreatorAccountId(long mostInterfaceId, long accountId) throws DatabaseException {
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
        final Long creatorAccountId = proposedMostInterface.getCreatorAccountId();

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
        final Query query = new Query("UPDATE interfaces SET is_deleted = ? WHERE id=?")
                .setParameter(isDeleted)
                .setParameter(mostInterfaceId)
                ;

        _databaseConnection.executeSql(query);
        // Child object associations are not marked as deleted in this case.
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

    private void _deleteMostInterfaceIfUnapproved(final long mostInterfaceId) throws DatabaseException {
        MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);
        MostInterface mostInterface = mostInterfaceInflater.inflateMostInterface(mostInterfaceId);

        if (! mostInterface.isApproved()) {
            // interface isn't approved and isn't associated with any function blocks, we can delete it
            _deleteMostFunctionsFromMostInterface(mostInterfaceId);
            _deleteReviewForMostInterface(mostInterfaceId);
            _deleteMostInterfaceFromDatabase(mostInterfaceId);
        }
    }

    private void _deleteMostFunctionsFromMostInterface(final long mostInterfaceId) throws DatabaseException {
        final MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(_databaseConnection);
        final List<MostFunction> mostFunctions = mostFunctionInflater.inflateMostFunctionsFromMostInterfaceId(mostInterfaceId, false);

        final MostFunctionDatabaseManager mostFunctionDatabaseManager = new MostFunctionDatabaseManager(_databaseConnection);
        for (final MostFunction mostFunction : mostFunctions) {
            // function is not approved, we can delete it.
            mostFunctionDatabaseManager.deleteMostFunctionFromMostInterface(mostInterfaceId, mostFunction.getId());
        }
    }

    private void _deleteMostInterfaceFromDatabase(final long mostInterfaceId) throws DatabaseException {
        _nullifyMostInterfaceParentRelationships(mostInterfaceId);

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

    public long forkMostInterface(final long mostInterfaceId, final Long parentFunctionBlockId, final long currentAccountId) throws DatabaseException {
        MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);
        MostInterface mostInterface = mostInterfaceInflater.inflateMostInterface(mostInterfaceId);

        final long newMostInterfaceId = _forkMostInterface(mostInterface);
        _copyMostInterfaceMostFunctions(mostInterfaceId, newMostInterfaceId);
        if (parentFunctionBlockId != null) {
            _associateMostInterfaceWithFunctionBlock(parentFunctionBlockId, newMostInterfaceId);
        }
        return newMostInterfaceId;
    }

    public void updateMostInterface(final MostInterface proposedMostInterface, final Long currentAccountId) throws DatabaseException {
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

    public void deleteMostInterfaceFromFunctionBlock(final long functionBlockId, final long mostInterfaceId) throws DatabaseException {
        if (functionBlockId > 0) {
            _disassociateMostInterfaceWithFunctionBlock(functionBlockId, mostInterfaceId);
        }
        else {
            if (!isOrphaned(mostInterfaceId)) {
                _disassociateMostInterfaceFromAllUnReleasedFunctionBlocks(mostInterfaceId);
            }
            else {
                _deleteMostInterfaceIfUnapproved(mostInterfaceId);
            }
        }
    }

    public List<Long> listFunctionBlocksContainingMostInterface(final long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("SELECT DISTINCT function_blocks_interfaces.function_block_id\n" +
                                        "FROM function_blocks_interfaces\n" +
                                        "WHERE function_blocks_interfaces.interface_id = ?"
        );
        query.setParameter(mostInterfaceId);

        List<Row> rows =_databaseConnection.query(query);
        final ArrayList<Long> functionBlockIds = new ArrayList<Long>();
        for (Row row : rows) {
            Long functionBlockId = row.getLong("function_block_id");
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

    public void submitMostInterfaceForReview(final long mostInterfaceId, final long submittingAccountId) throws DatabaseException {
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

    private void _submitMostInterfaceForReview(final long mostInterfaceId, final long submittingAccountId) throws DatabaseException {
        final Query query = new Query("INSERT INTO reviews (interface_id, account_id, created_date) VALUES (?, ?, NOW())");
        query.setParameter(mostInterfaceId);
        query.setParameter(submittingAccountId);

        _databaseConnection.executeSql(query);
    }

    public void approveMostInterface(final long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("UPDATE interfaces SET is_approved = ? WHERE id = ?")
                .setParameter(true)
                .setParameter(mostInterfaceId);

        _databaseConnection.executeSql(query);

        _approveMostFunctionsForMostInterfaceId(mostInterfaceId);
    }

    private void _approveMostFunctionsForMostInterfaceId(final long mostInterfaceId) throws DatabaseException {
        final MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(_databaseConnection);
        final List<MostFunction> mostFunctions = mostFunctionInflater.inflateMostFunctionsFromMostInterfaceId(mostInterfaceId, false);

        final MostFunctionDatabaseManager mostFunctionDatabaseManager = new MostFunctionDatabaseManager(_databaseConnection);
        for (final MostFunction mostFunction : mostFunctions) {
            mostFunctionDatabaseManager.approveMostFunction(mostFunction.getId());
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
}