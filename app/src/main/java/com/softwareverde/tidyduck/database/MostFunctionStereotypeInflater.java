package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.MostFunctionStereotype;
import com.softwareverde.tidyduck.Operation;

import java.sql.Connection;
import java.util.List;

public class MostFunctionStereotypeInflater {

    protected final DatabaseConnection<Connection> _databaseConnection;

    public MostFunctionStereotypeInflater(DatabaseConnection<Connection> connection) {
        _databaseConnection = connection;
    }

    public MostFunctionStereotype inflateStereotype(final Long mostFunctionStereotypeId) throws DatabaseException {
        final Query query = new Query(
                "SELECT id, name, supports_notification, category FROM operations WHERE id = ?"
        );
        query.setParameter(mostFunctionStereotypeId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Stereotype ID " + mostFunctionStereotypeId + " not found.");
        }
        final Row row = rows.get(0);

        final MostFunctionStereotype mostFunctionStereotype = new MostFunctionStereotype();
        mostFunctionStereotype.setId(row.getLong("id"));
        mostFunctionStereotype.setName(row.getString("name"));
        mostFunctionStereotype.setSupportsNotification(row.getBoolean("supports_notification"));
        mostFunctionStereotype.setCategory(row.getString("category"));
        mostFunctionStereotype.setOperations(inflateOperationsFromMostFunctionStereotypeId(mostFunctionStereotypeId));
        return mostFunctionStereotype;
    }

    public List<Operation> inflateOperationsFromMostFunctionStereotypeId(final long mostFunctionStereotypeId) throws DatabaseException {
        final Query query = new Query(
                "SELECT operation_id FROM function_stereotypes_operations WHERE function_stereotype_id = ?"
        );
        query.setParameter(mostFunctionStereotypeId);

        OperationInflater operationInflater = new OperationInflater(_databaseConnection);

        List<Operation> operations = new ArrayList<Operation>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final long operationId = row.getLong("operation_id");
            Operation operation = operationInflater.inflateOperation(operationId);
            operations.add(operation);
        }
        return operations;
    }

}