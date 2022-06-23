package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.database.row.Row;
import com.softwareverde.tidyduck.most.MostFunctionStereotype;
import com.softwareverde.tidyduck.most.Operation;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class StereotypeInflater {

    protected final DatabaseConnection<Connection> _databaseConnection;

    public StereotypeInflater(DatabaseConnection<Connection> connection) {
        _databaseConnection = connection;
    }

    private Operation inflateOperation(final Object object) { return null; } // TODO

    public MostFunctionStereotype inflateStereotype(final Long stereotypeId) throws DatabaseException {
        final Query query = new Query(
                "SELECT id, name, supports_notification, category FROM operations WHERE id = ?"
        );
        query.setParameter(stereotypeId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Stereotype ID " + stereotypeId + " not found.");
        }
        final Row row = rows.get(0);

        final MostFunctionStereotype stereotype = new MostFunctionStereotype();
        stereotype.setId(row.getLong("id"));
        stereotype.setName(row.getString("name"));
        stereotype.setSupportsNotification(row.getBoolean("supports_notification"));
        stereotype.setCategory(row.getString("category"));
        stereotype.setOperations(inflateOperationsFromStereotypeId(stereotypeId));
        return stereotype;
    }

    public List<Operation> inflateOperationsFromStereotypeId(final long stereotypeId) throws DatabaseException {
        final Query query = new Query(
            "SELECT operation_id FROM function_stereotypes_operations WHERE function_stereotype_id = ?"
        );
        query.setParameter(stereotypeId);

        List<Operation> operations = new ArrayList<Operation>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final long operationId = row.getLong("operation_id");
            final Operation operation = inflateOperation(operationId);
            operations.add(operation);
        }
        return operations;
    }

}
