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

public class MostFunctionStereotypeInflater {

    protected final DatabaseConnection<Connection> _databaseConnection;

    public MostFunctionStereotypeInflater(DatabaseConnection<Connection> connection) {
        _databaseConnection = connection;
    }

    public List<MostFunctionStereotype> inflateMostFunctionStereotypes() throws DatabaseException {
        final Query query = new Query("SELECT id FROM function_stereotypes");

        List<Row> rows = _databaseConnection.query(query);
        ArrayList<MostFunctionStereotype> stereotypes = new ArrayList<>();
        for (final Row row : rows) {
            MostFunctionStereotype mostFunctionStereotype = inflateMostFunctionStereotype(row.getLong("id"));
            stereotypes.add(mostFunctionStereotype);
        }
        return stereotypes;
    }

    public MostFunctionStereotype inflateMostFunctionStereotype(final long mostFunctionStereotypeId) throws DatabaseException {
        final Query query = new Query(
                "SELECT id, name, supports_notification, category FROM function_stereotypes WHERE id = ?"
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
