package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.database.row.Row;
import com.softwareverde.tidyduck.most.TypeDefinition;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class TypeDefinitionInflater {

    private final DatabaseConnection<Connection> _databaseConnection;

    public TypeDefinitionInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<TypeDefinition> inflateTypeDefinitions() throws DatabaseException {
        final Query query = new Query(
            "SELECT * FROM type_definitions"
        );

        final List<TypeDefinition> typeDefinitions = new ArrayList<TypeDefinition>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final TypeDefinition typeDefinition = _convertToTypeDefinition(row);
            typeDefinitions.add(typeDefinition);
        }
        return typeDefinitions;
    }

    private TypeDefinition _convertToTypeDefinition(final Row row) throws DatabaseException {
        final Long id = row.getLong("id");

        final String typeId = row.getString("type_id");
        final String typeName = row.getString("type_name");
        final Integer typeSize = row.getInteger("type_size");
        final String typeDescription = row.getString("type_description");

        return new TypeDefinition(typeId, typeSize, typeName, typeDescription);
    }
}
