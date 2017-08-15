package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.most.ErrorDefinition;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ErrorDefinitionInflater {

    private final DatabaseConnection<Connection> _databaseConnection;

    public ErrorDefinitionInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<ErrorDefinition> inflateErrorDefinitions() throws DatabaseException {
        final Query query = new Query(
            "SELECT * FROM error_definitions"
        );

        final List<ErrorDefinition> errorDefinitions = new ArrayList<ErrorDefinition>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final ErrorDefinition errorDefinition = _convertToErrorDefinition(row);
            errorDefinitions.add(errorDefinition);
        }
        return errorDefinitions;
    }

    private ErrorDefinition _convertToErrorDefinition(final Row row) throws DatabaseException {
        final Long id = row.getLong("id");

        final String errorId = row.getString("error_id");
        final String errorCode = row.getString("error_code");
        final String errorDescription = row.getString("error_description");
        final String info = row.getString("info_code");
        final String infoDescription = row.getString("info_description");

        return new ErrorDefinition(errorId, errorCode, errorDescription, info, infoDescription);
    }
}
