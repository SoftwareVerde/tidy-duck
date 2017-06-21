package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.tidyduck.MostInterface;

public class MostInterfaceDatabaseManager {
    private final DatabaseConnection _databaseConnection;

    public MostInterfaceDatabaseManager(final DatabaseConnection databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public void insertMostInterfaceForFunctionBlock(final long functionBlockId, final MostInterface mostInterface) throws DatabaseException {
        // TODO: check to see whether function block is committed.
        _insertMostInterface(mostInterface);
        _associateMostInterfaceWithFunctionBlock(functionBlockId, mostInterface.getId());
    }

    private void _insertMostInterface(final MostInterface mostInterface) throws DatabaseException {
        final String mostId = mostInterface.getMostId();
        final String name = mostInterface.getName();
        final String description = mostInterface.getDescription();
        final String version = mostInterface.getVersion();

        final Query query = new Query("INSERT INTO interfaces (most_id, name, description, last_modified_date, version) VALUES (?, ?, ?, NOW(), ?)")
                .setParameter(mostId)
                .setParameter(name)
                .setParameter(description)
                .setParameter(version)
        ;

        final long mostInterfaceId = _databaseConnection.executeSql(query);
        mostInterface.setId(mostInterfaceId);
    }

    private Long _associateMostInterfaceWithFunctionBlock(final long functionBlockId, final long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("INSERT INTO function_blocks_interfaces (function_block_id, interface_id) VALUES (?, ?)")
                .setParameter(functionBlockId)
                .setParameter(mostInterfaceId)
                ;

        return _databaseConnection.executeSql(query);
    }

    public void updateMostInterfaceForFunctionBlock (final long functionBlockId, final MostInterface proposedMostInterface) throws DatabaseException {
        final long inputMostInterfaceId = proposedMostInterface.getId();

        MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);
        MostInterface databaseMostInterface = mostInterfaceInflater.inflateMostInterface(inputMostInterfaceId);
        if (!databaseMostInterface.isCommitted()) {
            // not committed, can update existing function block
            _updateUncommittedMostInterface(proposedMostInterface);
        } else {
            // current block is committed to a function catalog
            // need to insert a new function block replace this one
            _insertMostInterface(proposedMostInterface);
            final long newMostInterfaceId = proposedMostInterface.getId();
            // change association with function catalog
            _disassociateMostInterfaceWithFunctionBlock(functionBlockId, inputMostInterfaceId);
            _associateMostInterfaceWithFunctionBlock(functionBlockId, newMostInterfaceId);
        }
    }
    
    private void _updateUncommittedMostInterface(MostInterface proposedMostInterface) throws DatabaseException {
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

    public void deleteMostInterfaceFromFunctionBlock(final long functionBlockId, final long mostInterfaceId) throws DatabaseException {
        _disassociateMostInterfaceWithFunctionBlock(functionBlockId, mostInterfaceId);
        _deleteMostInterfaceIfUncommited(mostInterfaceId);
    }

    private void _disassociateMostInterfaceWithFunctionBlock(long functionBlockId, long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("DELETE FROM function_blocks_interfaces WHERE function_block_id = ? and interface_id = ?")
                .setParameter(functionBlockId)
                .setParameter(mostInterfaceId)
                ;

        _databaseConnection.executeSql(query);
    }

    private void _deleteMostInterfaceIfUncommited(long mostInterfaceId) throws DatabaseException {
        MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);
        MostInterface mostInterface = mostInterfaceInflater.inflateMostInterface(mostInterfaceId);

        if (!mostInterface.isCommitted()) {
            // function block isn't committed, we can delete it
            // TODO: delete interfaces from function block
            _deleteMostInterfaceFromDatabase(mostInterfaceId);
        }
    }

    private void _deleteMostInterfaceFromDatabase(final long mostInterfaceId) throws DatabaseException {
        final Query query = new Query("DELETE FROM interfaces WHERE id = ?")
                .setParameter(mostInterfaceId)
                ;

        _databaseConnection.executeSql(query);
    }
}