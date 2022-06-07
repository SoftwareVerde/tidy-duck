package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.database.row.Row;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.most.*;

import java.sql.Connection;
import java.util.*;

public class PropertyReportDefinitionInflater {

    private final DatabaseConnection<Connection> _databaseConnection;

    public PropertyReportDefinitionInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<PropertyReportDefinition> inflateReportDefinitions() throws DatabaseException {
        final Query query = new Query(
            "SELECT * FROM property_report_definitions"
        );

        final List<PropertyReportDefinition> reportDefinitions = new ArrayList<PropertyReportDefinition>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final PropertyReportDefinition classDefinition = _convertToReportDefinition(row);
            reportDefinitions.add(classDefinition);
        }
        return reportDefinitions;
    }

    private PropertyReportDefinition _convertToReportDefinition(final Row row) throws DatabaseException {
        final Long id = row.getLong("id");

        final String reportId = row.getString("report_id");
        final String reportOperationType = row.getString("report_operation_type");
        final String reportName = row.getString("report_name");
        final String reportDescription = row.getString("report_description");

        return new PropertyReportDefinition(reportId, reportOperationType, reportName, reportDescription);
    }
}
