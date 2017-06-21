package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.tidyduck.MostInterface;
import jdk.nashorn.internal.objects.annotations.Function;

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
}