package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.MostFunction;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MostFunctionInflater {
    private final DatabaseConnection<Connection> _databaseConnection;

    public MostFunctionInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<MostFunction> inflateMostFunctionsFromMostInterfaceId(long mostInterfaceId) throws DatabaseException {
        final Query query = new Query(
            "SELECT function_id FROM interfaces_functions WHERE interface_id = ?"
        );
        query.setParameter(mostInterfaceId);

        List<MostFunction> mostFunctions = new ArrayList<MostFunction>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final long mostFunctionId = row.getLong("function_id");
            MostFunction mostFunction = inflateMostFunction(mostFunctionId);
            mostFunctions.add(mostFunction);
        }
        return mostFunctions;
    }

    public MostFunction inflateMostFunction(final long mostFunctionId) throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM functions WHERE id = ?"
        );
        query.setParameter(mostFunctionId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Function ID " + mostFunctionId + " not found.");
        }

        final Row row = rows.get(0);

        final Long id = row.getLong("id");
        final String mostId = row.getString("most_id");
        final String name = row.getString("name");
        final String description = row.getString("description");
        final Date lastModifiedDate = DateUtil.dateFromDateString(row.getString("last_modified_date"));
        final String releaseVersion = row.getString("release_version");
        final boolean isCommitted = row.getBoolean("is_committed");

        /*
        MostFunction mostFunction = new MostFunction();
        mostFunction.setId(id);
        mostFunction.setMostId(mostId);
        mostFunction.setName(name);
        mostFunction.setDescription(description);
        mostFunction.setLastModifiedDate(lastModifiedDate);
        mostFunction.setRelease(releaseVersion);
        mostInterface.setCommitted(isCommitted);
        */

        /*
        Need inflaters for Types, Stereotypes, and Operations. Parameters are derived from operations and stereotypes...
         */

        return null;
    }
}
