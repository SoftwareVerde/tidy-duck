package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
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

        final Query query = new Query("INSERT INTO interfaces (most_id, name, description, last_modified_date, version, prior_version_id) VALUES (?, ?, ?, NOW(), ?, ?)")
            .setParameter(mostId)
            .setParameter(name)
            .setParameter(description)
            .setParameter(version)
            .setParameter(priorVersionId)
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

    private void _setBaseVersionId(long mostInterfaceId, long baseVersionId) throws DatabaseException {
        final Query query = new Query("UPDATE interfaces SET base_version_id = ? WHERE id = ?")
            .setParameter(baseVersionId)
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

    private void _updateUnreleasedMostInterface(MostInterface proposedMostInterface) throws DatabaseException {
        final String newMostId = proposedMostInterface.getMostId();
        final String newName = proposedMostInterface.getName();
        final String newVersion = proposedMostInterface.getVersion();
        final String newDescription = proposedMostInterface.getDescription();
        final long mostInterfaceId = proposedMostInterface.getId();

        final Query query = new Query("UPDATE interfaces SET most_id = ?, name = ?, description = ?, last_modified_date = NOW(), version = ? WHERE id = ?")
            .setParameter(newMostId)
            .setParameter(newName)
            .setParameter(newDescription)
            .setParameter(newVersion)
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
                    "WHERE function_blocks.is_released=0)")
                .setParameter(mostInterfaceId);

        _databaseConnection.executeSql(query);
    }

    private void _deleteMostInterfaceIfUnreleased(final long mostInterfaceId) throws DatabaseException {
        MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);
        MostInterface mostInterface = mostInterfaceInflater.inflateMostInterface(mostInterfaceId);

        if (!mostInterface.isReleased()) {
            // interface isn't released and isn't associated with any function blocks, we can delete it
            _deleteMostFunctionsFromMostInterface(mostInterfaceId);
            _deleteMostInterfaceFromDatabase(mostInterfaceId);
        }
    }

    private void _deleteMostFunctionsFromMostInterface(final long mostInterfaceId) throws DatabaseException {
        final MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(_databaseConnection);
        final List<MostFunction> mostFunctions = mostFunctionInflater.inflateMostFunctionsFromMostInterfaceId(mostInterfaceId);

        final MostFunctionDatabaseManager mostFunctionDatabaseManager = new MostFunctionDatabaseManager(_databaseConnection);
        for (final MostFunction mostFunction : mostFunctions) {
            // function is not released, we can delete it.
            mostFunctionDatabaseManager.deleteMostFunctionFromMostInterface(mostInterfaceId, mostFunction.getId());
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
        // TODO: check to see whether function block is released.
        _insertMostInterface(mostInterface);
        _associateMostInterfaceWithFunctionBlock(functionBlockId, mostInterface.getId());
    }

    public void insertOrphanedMostInterface(final MostInterface mostInterface) throws DatabaseException {
        _insertMostInterface(mostInterface);
    }

    public void updateMostInterfaceForFunctionBlock (final long functionBlockId, final MostInterface proposedMostInterface) throws DatabaseException {
        final long inputMostInterfaceId = proposedMostInterface.getId();

        MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);
        MostInterface originalMostInterface = mostInterfaceInflater.inflateMostInterface(inputMostInterfaceId);
        if (!originalMostInterface.isReleased()) {
            // not released, can update existing interface
            _updateUnreleasedMostInterface(proposedMostInterface);
        } else {
            // current block is released, need to insert a new interface replace this one
            _insertMostInterface(proposedMostInterface, originalMostInterface);
            final long newMostInterfaceId = proposedMostInterface.getId();
            _copyMostInterfaceMostFunctions(inputMostInterfaceId, newMostInterfaceId);
            // change association with function block if id isn't 0
            if (functionBlockId != 0) {
                // TODO: check if function block is released?
                _disassociateMostInterfaceWithFunctionBlock(functionBlockId, inputMostInterfaceId);
                _associateMostInterfaceWithFunctionBlock(functionBlockId, newMostInterfaceId);
            }
        }
    }

    private void _copyMostInterfaceMostFunctions(final long originalMostInterfaceId, final long newMostInterfaceId) throws DatabaseException {
        final MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(_databaseConnection);
        final List<MostFunction> mostFunctions = mostFunctionInflater.inflateMostFunctionsFromMostInterfaceId(originalMostInterfaceId);

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
                _deleteMostInterfaceIfUnreleased(mostInterfaceId);
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
}