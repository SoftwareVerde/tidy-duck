package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.database.row.Row;
import com.softwareverde.tidyduck.most.Operation;

import java.sql.Connection;
import java.util.List;

public class OperationInflater {

    protected final DatabaseConnection<Connection> _databaseConnection;

    public OperationInflater(DatabaseConnection<Connection> connection) {
        _databaseConnection = connection;
    }

    public Operation inflateOperation(final long operationId) throws DatabaseException {
        final Query query = new Query(
                "SELECT id, name, opcode, is_input FROM operations WHERE id = ?"
        );
        query.setParameter(operationId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Operation ID " + operationId + " not found.");
        }
        final Row row = rows.get(0);

        final Operation operation = new Operation();
        operation.setId(row.getLong("id"));
        operation.setName(row.getString("name"));
        operation.setOpcode(row.getString("opcode"));
        operation.setInput(row.getBoolean("is_input"));
        return operation;
    }

}
