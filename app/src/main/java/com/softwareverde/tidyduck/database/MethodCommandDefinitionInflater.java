package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.database.row.Row;
import com.softwareverde.tidyduck.most.*;

import java.sql.Connection;
import java.util.*;

public class MethodCommandDefinitionInflater {

    private final DatabaseConnection<Connection> _databaseConnection;

    public MethodCommandDefinitionInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<MethodCommandDefinition> inflateCommandDefinitions() throws DatabaseException {
        final Query query = new Query(
            "SELECT * FROM method_command_definitions"
        );

        final List<MethodCommandDefinition> commandDefinitions = new ArrayList<MethodCommandDefinition>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final MethodCommandDefinition classDefinition = _convertToCommandDefinition(row);
            commandDefinitions.add(classDefinition);
        }
        return commandDefinitions;
    }

    private MethodCommandDefinition _convertToCommandDefinition(final Row row) throws DatabaseException {
        final Long id = row.getLong("id");

        final String commandId = row.getString("command_id");
        final String commandOperationType = row.getString("command_operation_type");
        final String commandName = row.getString("command_name");
        final String commandDescription = row.getString("command_description");

        return new MethodCommandDefinition(commandId, commandOperationType, commandName, commandDescription);
    }
}
