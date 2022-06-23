package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.database.row.Row;
import com.softwareverde.tidyduck.most.UnitDefinition;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class UnitDefinitionInflater {

    private final DatabaseConnection<Connection> _databaseConnection;

    public UnitDefinitionInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<UnitDefinition> inflateUnitDefinitions() throws DatabaseException {
        final Query query = new Query(
            "SELECT * FROM unit_definitions"
        );

        final List<UnitDefinition> unitDefinitions = new ArrayList<UnitDefinition>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final UnitDefinition unitDefinition = _convertToUnitDefinition(row);
            unitDefinitions.add(unitDefinition);
        }
        return unitDefinitions;
    }

    private UnitDefinition _convertToUnitDefinition(final Row row) throws DatabaseException {
        final Long id = row.getLong("id");

        final String unitId = row.getString("unit_id");
        final String unitName = row.getString("unit_name");
        final String unitCode = row.getString("unit_code");
        final String unitGroup = row.getString("unit_group");

        return new UnitDefinition(unitId, unitCode, unitName, unitGroup);
    }
}
