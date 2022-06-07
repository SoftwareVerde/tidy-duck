package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.database.row.Row;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.most.*;

import java.sql.Connection;
import java.util.*;

public class ClassDefinitionInflater {

    private final DatabaseConnection<Connection> _databaseConnection;

    public ClassDefinitionInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<ClassDefinition> inflateClassDefinitions() throws DatabaseException {
        final Query query = new Query(
            "SELECT * FROM class_definitions"
        );

        final List<ClassDefinition> classDefinitions = new ArrayList<ClassDefinition>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final ClassDefinition classDefinition = _convertToClassDefinition(row);
            classDefinitions.add(classDefinition);
        }
        return classDefinitions;
    }

    private ClassDefinition _convertToClassDefinition(final Row row) throws DatabaseException {
        final Long id = row.getLong("id");

        final String classId = row.getString("class_id");
        final String className = row.getString("class_name");
        final String classDescription = row.getString("class_description");

        return new ClassDefinition(classId, className, classDescription);
    }
}
