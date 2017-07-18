package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.most.MostType;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class MostTypeInflater {

    protected final DatabaseConnection<Connection> _databaseConnection;

    public MostTypeInflater(DatabaseConnection<Connection> connection) {
        _databaseConnection = connection;
    }

    public List<MostType> inflateMostTypes() throws DatabaseException {
        final Query query = new Query("SELECT id FROM most_types");

        List<Row> rows = _databaseConnection.query(query);
        ArrayList<MostType> types = new ArrayList<>();
        for (final Row row : rows) {
            MostType mostType = inflateMostType(row.getLong("id"));
            types.add(mostType);
        }
        return types;
    }

    public MostType inflateMostType(final Long mostTypeId) throws DatabaseException {
        final Query query = new Query(
                "SELECT id, name FROM most_types WHERE id = ?"
        );
        query.setParameter(mostTypeId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Most Type ID " + mostTypeId + " not found.");
        }
        final Row row = rows.get(0);

        final MostType mostType = new MostType();
        mostType.setId(row.getLong("id"));
        mostType.setName(row.getString("name"));
        return mostType;
    }

}